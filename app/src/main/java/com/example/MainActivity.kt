package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MutationLogEntity
import com.example.data.SpecimenEntity
import com.example.data.XenoGenDatabase
import com.example.data.XenoGenRepository
import com.example.ui.components.LabHeader
import com.example.ui.screens.ArchiveScreen
import com.example.ui.screens.EvolutionTreeScreen
import com.example.ui.screens.HubContent
import com.example.ui.screens.HubScreen
import com.example.ui.screens.MutationChamberScreen
import com.example.ui.screens.SimulationScreen
import com.example.ui.theme.LabSurfaceContainerLowest
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LabNavTab
import com.example.ui.viewmodel.XenoGenViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        XenoGenApp()
      }
    }
  }
}

@Composable
fun XenoGenApp() {
  if (LocalInspectionMode.current) {
    XenoGenPreviewApp()
    return
  }

  val context = LocalContext.current
  val repository = remember {
    val database = XenoGenDatabase.getInstance(context)
    XenoGenRepository(database.specimenDao())
  }

  val viewModel: XenoGenViewModel = viewModel(
    factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return XenoGenViewModel(repository) as T
      }
    }
  )

  val currentTab by viewModel.currentTab.collectAsState()

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(LabSurfaceContainerLowest),
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      LabHeader(
        currentTab = currentTab,
        onTabSelected = { viewModel.selectTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "tab_transition"
      ) { targetTab ->
        when (targetTab) {
          LabNavTab.HUB -> HubScreen(viewModel = viewModel)
          LabNavTab.MUTATION -> MutationChamberScreen(viewModel = viewModel)
          LabNavTab.EVOLUTION -> EvolutionTreeScreen(viewModel = viewModel)
          LabNavTab.ARCHIVE -> ArchiveScreen(viewModel = viewModel)
          LabNavTab.SIMULATION -> SimulationScreen(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun XenoGenPreviewApp(initialTab: LabNavTab = LabNavTab.HUB) {
  var currentTab by remember { mutableStateOf(initialTab) }

  val sampleSpecimen = remember {
    SpecimenEntity(
      specimenCode = "SPECIMEN-X9",
      commonName = "Obsidian Stalker",
      genus = "Draconic Void",
      originPlanet = "Kepler-16b Deep Basin",
      status = "MUTATING",
      dnaSequence = "AGT-X12-FIRE-789-WING",
      aggression = 90,
      intelligence = 60,
      venomGlands = true,
      dermalHardening = 78,
      bioLuminescence = 85,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAWtsnMLGWdH1qt3ktGxnSW_7mC3UIHxwjnEFA_jbDSsgfxnCHtlBHRaBtpYCia1BVynE0hRGKuSLgGuY_GCR0JmLfjUjh730Hdr3eWNuIMd0yGJUNFak8jnYN_HXacsnXPehGbptcEtPrmS9UEq0uKOwExtsR8Rx-jwNtllUY2NKmrhzqxa4dJSby19Gax5rDs1RlL-qh4tUEQcOktPhUAPpoz6caOpw1Z2WorqK7k00A4UQQ2pWLuSa-D5rU24gLhXzsaeV_DSKQ",
      habitatType = "Void",
      notes = "Chitinous exoskeleton infused with silicates. Bioluminescent channels active."
    )
  }

  val sampleLogs = remember {
    listOf(
      MutationLogEntity(
        specimenCode = "SPECIMEN-X9",
        message = "Dermal hardness fortified +12% [96.4% OPTIMAL]",
        timeLabel = "10:24:18",
        tag = "SPLICED"
      ),
      MutationLogEntity(
        specimenCode = "SPECIMEN-X9",
        message = "Synthesized nucleotide sequence AGT-X12",
        timeLabel = "10:14:02",
        tag = "MUTATION"
      )
    )
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(LabSurfaceContainerLowest),
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      LabHeader(
        currentTab = currentTab,
        onTabSelected = { currentTab = it }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      HubContent(
        currentSpecimen = sampleSpecimen,
        dnaSeq = "AGT-X12-FIRE-789-WING",
        isGenerating = false,
        logs = sampleLogs,
        onGenerateCreature = {}
      )
    }
  }
}

@Preview(name = "XenoGen Lab - Phone", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun XenoGenAppPreview() {
  MyApplicationTheme {
    XenoGenPreviewApp()
  }
}

@Preview(name = "XenoGen Lab - Tablet", showBackground = true, widthDp = 800, heightDp = 1200)
@Composable
fun XenoGenAppTabletPreview() {
  MyApplicationTheme {
    XenoGenPreviewApp()
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}

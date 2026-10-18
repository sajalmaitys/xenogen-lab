package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MutationLogEntity
import com.example.data.SpecimenEntity
import com.example.data.XenoGenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class LabNavTab(val title: String) {
    HUB("Hub"),
    MUTATION("Chamber"),
    EVOLUTION("Evolution"),
    ARCHIVE("Archive"),
    SIMULATION("Kepler-16b")
}

class XenoGenViewModel(private val repository: XenoGenRepository) : ViewModel() {

    val allSpecimens: StateFlow<List<SpecimenEntity>> = repository.allSpecimens
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mutationLogs: StateFlow<List<MutationLogEntity>> = repository.allMutationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(LabNavTab.HUB)
    val currentTab: StateFlow<LabNavTab> = _currentTab.asStateFlow()

    // Hub State
    private val _hubSpecimenCode = MutableStateFlow("SPECIMEN-X9")
    val hubSpecimenCode: StateFlow<String> = _hubSpecimenCode.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _dnaSequence = MutableStateFlow("AGT-X12-FIRE-789-WING")
    val dnaSequence: StateFlow<String> = _dnaSequence.asStateFlow()

    // Mutation Chamber State
    private val _chamberSpecimenCode = MutableStateFlow("ALPHA-007")
    val chamberSpecimenCode: StateFlow<String> = _chamberSpecimenCode.asStateFlow()

    private val _neuralAggression = MutableStateFlow(85f)
    val neuralAggression: StateFlow<Float> = _neuralAggression.asStateFlow()

    private val _bioLuminescence = MutableStateFlow(90f)
    val bioLuminescence: StateFlow<Float> = _bioLuminescence.asStateFlow()

    private val _dermalHardening = MutableStateFlow(82f)
    val dermalHardening: StateFlow<Float> = _dermalHardening.asStateFlow()

    private val _isExecutingMutation = MutableStateFlow(false)
    val isExecutingMutation: StateFlow<Boolean> = _isExecutingMutation.asStateFlow()

    private val _mutationStatusBanner = MutableStateFlow<String?>(null)
    val mutationStatusBanner: StateFlow<String?> = _mutationStatusBanner.asStateFlow()

    // Evolution Tree State
    private val _isEcosystemSimulationActive = MutableStateFlow(true)
    val isEcosystemSimulationActive: StateFlow<Boolean> = _isEcosystemSimulationActive.asStateFlow()

    private val _selectedEvoNode = MutableStateFlow(2) // 0: Base, 1: Arcton-1, 2: Cryonix
    val selectedEvoNode: StateFlow<Int> = _selectedEvoNode.asStateFlow()

    // Archive State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedClassFilter = MutableStateFlow("ALL")
    val selectedClassFilter: StateFlow<String> = _selectedClassFilter.asStateFlow()

    private val _selectedSpecimenForDetail = MutableStateFlow<SpecimenEntity?>(null)
    val selectedSpecimenForDetail: StateFlow<SpecimenEntity?> = _selectedSpecimenForDetail.asStateFlow()

    private val _isAddSpecimenDialogOpen = MutableStateFlow(false)
    val isAddSpecimenDialogOpen: StateFlow<Boolean> = _isAddSpecimenDialogOpen.asStateFlow()

    // Simulation Vitals
    private val _simulationIntegrity = MutableStateFlow(72)
    val simulationIntegrity: StateFlow<Int> = _simulationIntegrity.asStateFlow()

    private val _simulationPulse = MutableStateFlow(120)
    val simulationPulse: StateFlow<Int> = _simulationPulse.asStateFlow()

    private val _isSimulationStabilizing = MutableStateFlow(false)
    val isSimulationStabilizing: StateFlow<Boolean> = _isSimulationStabilizing.asStateFlow()

    // Filtered Specimens in Archive
    val filteredSpecimens: StateFlow<List<SpecimenEntity>> = combine(
        allSpecimens,
        _searchQuery,
        _selectedClassFilter
    ) { specimens, query, filter ->
        specimens.filter { specimen ->
            val matchesQuery = query.isBlank() ||
                    specimen.commonName.contains(query, ignoreCase = true) ||
                    specimen.specimenCode.contains(query, ignoreCase = true) ||
                    specimen.genus.contains(query, ignoreCase = true) ||
                    specimen.originPlanet.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "ALL" -> true
                "INSECTOID" -> specimen.genus.contains("Insectoid", ignoreCase = true)
                "AQUATIC" -> specimen.genus.contains("Aquatic", ignoreCase = true)
                "REPTILIAN" -> specimen.genus.contains("Reptilian", ignoreCase = true)
                "AVIAN" -> specimen.genus.contains("Avian", ignoreCase = true)
                "TECTONIC" -> specimen.genus.contains("Tectonic", ignoreCase = true)
                "VOID" -> specimen.genus.contains("Void", ignoreCase = true) || specimen.genus.contains("Xenomorph", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: LabNavTab) {
        _currentTab.value = tab
    }

    fun selectSpecimenForDetail(specimen: SpecimenEntity?) {
        _selectedSpecimenForDetail.value = specimen
    }

    fun openAddSpecimenDialog(open: Boolean) {
        _isAddSpecimenDialogOpen.value = open
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setClassFilter(filter: String) {
        _selectedClassFilter.value = filter
    }

    fun setNeuralAggression(value: Float) {
        _neuralAggression.value = value
    }

    fun setBioLuminescence(value: Float) {
        _bioLuminescence.value = value
    }

    fun setDermalHardening(value: Float) {
        _dermalHardening.value = value
    }

    fun toggleEcosystemSimulation() {
        _isEcosystemSimulationActive.value = !_isEcosystemSimulationActive.value
    }

    fun selectEvoNode(index: Int) {
        _selectedEvoNode.value = index
    }

    fun switchChamberSpecimen(code: String) {
        _chamberSpecimenCode.value = code
        // load current specimen modifiers if available
        val spec = allSpecimens.value.find { it.specimenCode == code }
        if (spec != null) {
            _neuralAggression.value = spec.aggression.toFloat()
            _bioLuminescence.value = spec.bioLuminescence.toFloat()
            _dermalHardening.value = spec.dermalHardening.toFloat()
        }
    }

    fun generateNewCreature() {
        viewModelScope.launch {
            _isGenerating.value = true
            val bases = listOf("AGT", "TCG", "CYT", "XEN", "OMEGA", "NEUR")
            val codons = (1..4).joinToString("-") {
                val prefix = bases.random()
                val num = Random.nextInt(10, 99)
                "$prefix$num"
            }
            delay(1200) // Synthesizing animation
            _dnaSequence.value = codons
            _isGenerating.value = false

            repository.addMutationLog(
                specimenCode = _hubSpecimenCode.value,
                message = "New DNA Sequence synthesized: $codons.",
                tag = "SPLICED"
            )
        }
    }

    fun executeMutation() {
        viewModelScope.launch {
            _isExecutingMutation.value = true
            delay(900)
            repository.updateSpecimenModifiers(
                specimenCode = _chamberSpecimenCode.value,
                aggression = _neuralAggression.value.toInt(),
                bioLuminescence = _bioLuminescence.value.toInt(),
                dermalHardening = _dermalHardening.value.toInt()
            )
            _isExecutingMutation.value = false
            _mutationStatusBanner.value = "Mutation Sequence Executed for ${_chamberSpecimenCode.value}"
            delay(3000)
            _mutationStatusBanner.value = null
        }
    }

    fun emergencyPurge() {
        viewModelScope.launch {
            _neuralAggression.value = 40f
            _bioLuminescence.value = 30f
            _dermalHardening.value = 50f
            repository.addMutationLog(
                specimenCode = _chamberSpecimenCode.value,
                message = "EMERGENCY PURGE executed: Core stabilizers neutralized.",
                tag = "ALERT"
            )
            _mutationStatusBanner.value = "EMERGENCY PURGE COMPLETE: Modifiers Normalized"
            delay(3000)
            _mutationStatusBanner.value = null
        }
    }

    fun createCustomSpecimen(
        code: String,
        name: String,
        genus: String,
        origin: String,
        habitat: String,
        aggression: Int,
        intelligence: Int,
        notes: String
    ) {
        viewModelScope.launch {
            // Select an alien specimen image
            val sampleImages = listOf(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAWtsnMLGWdH1qt3ktGxnSW_7mC3UIHxwjnEFA_jbDSsgfxnCHtlBHRaBtpYCia1BVynE0hRGKuSLgGuY_GCR0JmLfjUjh730Hdr3eWNuIMd0yGJUNFak8jnYN_HXacsnXPehGbptcEtPrmS9UEq0uKOwExtsR8Rx-jwNtllUY2NKmrhzqxa4dJSby19Gax5rDs1RlL-qh4tUEQcOktPhUAPpoz6caOpw1Z2WorqK7k00A4UQQ2pWLuSa-D5rU24gLhXzsaeV_DSKQ",
                "https://lh3.googleusercontent.com/aida/ADBb0uiZTlaCg5Wvz_nwytR5G-PBqD-d_3B_rvEF8BApXZ_pS2ksFRV23dh7FAI7A5Kdg3DkkO8vXyMoR6cJqjG4agTSgNfBL05SDljLQ_TSgFFoLZ6nhqQo9tU9vyDb3lhvQaW2EehNTQPK1TgXF6TyuKHdPbc5VemcThVTO9b1Gwto-3A28yMUHHin9e32kMUAT_P1uNe-wR5dh44eHwNNzb7FuKdJkEBWJJ7jFstDd7sW9o6ftwft39K88A",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDAPesBenVlbqnMDkTxp1wasVaeG2_a-hGEmKqjj_s0LgshUGsmjqAscYhZ9dw-5aaHDZBqvt0avCO4FArxgJztA0X1QEjSsaEXxsTApyCSBbaXmkpKoY6PkewjmvHvJAJEZA1YHaUqA5UoEU11JToIlM36qgYOhTZ8AytAR-GwUFj2f4E2qG8OlyHBEvtuBM2lG1fJHshbuTBHd4TQ-1mEYs-ehlXa-yWRSbFA7SoPflLahOP1DZgxBmEqa7mHs5brcRfsj0KD8ls",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDfCeNF1sZ01ojwOognlX4DOLCnaD7-_sQvrXue2aN7usKmGvXwHSU8VRmOXefeE3Bh3NeCWcUASUtO70dpFp7kMf5MEcH1tCu76PZ_opC-KlrRT0zHbxvKAZgvRuPNyWR_zzOdkpLRqxwMUzJeC79t6aofmoGQQPusAnm9YtGEBBYVvwleF_j1pDWH_B8mVa6_LB4Jisk-RmPvGZo4nsMtcirVggzkO7Z0qlr8yZMgmoOimVRIsgAvaqL-FUU2jx1_LM3qVkJysGk",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDPcu6A0s-MA7VN4bA9HJlNkCly3vo5y7htGmkDwPK6ULpghvDhM-ReHXrpx7UiUp2CCMLb0_XU65IKnu4jepILTjSAeQNSEampdHceJ7D1BbtJLEK-pxW99SGihtSwpseYvSlYZwzYn036inz6JwnNsPD0zLI6FbLaYAbqchUFTF4vvivnhQPxW-ADO7uvXBibINNgR-ilCwRxpYWcDl3q9aYY0FnPjzsNOeOqiKVVv2sWNzdZLEHwxX1-wNXHQ-yCF1YKBLpj00s"
            )
            val randomImg = sampleImages.random()
            val newSpecimen = SpecimenEntity(
                specimenCode = code.ifBlank { "SPECIMEN-X${Random.nextInt(10, 99)}" },
                commonName = name.ifBlank { "Synthesized Bioform" },
                genus = genus.ifBlank { "Draconic Void" },
                originPlanet = origin.ifBlank { "Kepler-16b" },
                status = "MUTATING",
                dnaSequence = "SYN-${Random.nextInt(100, 999)}-ALN",
                aggression = aggression,
                intelligence = intelligence,
                venomGlands = true,
                dermalHardening = 70,
                bioLuminescence = 80,
                imageUrl = randomImg,
                habitatType = habitat.ifBlank { "Bio" },
                notes = notes.ifBlank { "User engineered genetic sequence. Initial vitals stabilized in stasis tank." }
            )
            repository.addNewSpecimen(newSpecimen)
            repository.addMutationLog(
                specimenCode = newSpecimen.specimenCode,
                message = "New specimen sequenced and added to vault: ${newSpecimen.commonName}.",
                tag = "SPLICED"
            )
            _isAddSpecimenDialogOpen.value = false
        }
    }

    fun deleteSpecimen(id: Int, code: String) {
        viewModelScope.launch {
            repository.deleteSpecimen(id)
            repository.addMutationLog(
                specimenCode = code,
                message = "Specimen $code purged from Archive vault.",
                tag = "ALERT"
            )
            if (_selectedSpecimenForDetail.value?.id == id) {
                _selectedSpecimenForDetail.value = null
            }
        }
    }

    fun stabilizeAtmosphere() {
        viewModelScope.launch {
            _isSimulationStabilizing.value = true
            delay(1200)
            _simulationIntegrity.value = (_simulationIntegrity.value + 12).coerceAtMost(100)
            _simulationPulse.value = 105
            _isSimulationStabilizing.value = false
            repository.addMutationLog(
                specimenCode = "KEPLER-16B",
                message = "Atmospheric bio-scrubbers activated. Climate stability restored.",
                tag = "INFO"
            )
        }
    }

    fun transferToChamber(specimen: SpecimenEntity) {
        _chamberSpecimenCode.value = specimen.specimenCode
        _neuralAggression.value = specimen.aggression.toFloat()
        _bioLuminescence.value = specimen.bioLuminescence.toFloat()
        _dermalHardening.value = specimen.dermalHardening.toFloat()
        _selectedSpecimenForDetail.value = null
        _currentTab.value = LabNavTab.MUTATION
    }
}

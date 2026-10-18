package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class XenoGenRepository(private val specimenDao: SpecimenDao) {

    val allSpecimens: Flow<List<SpecimenEntity>> = specimenDao.getAllSpecimens()
    val allMutationLogs: Flow<List<MutationLogEntity>> = specimenDao.getAllMutationLogs()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val count = specimenDao.getSpecimenCount()
        if (count == 0) {
            val defaultSpecimens = listOf(
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
                    notes = "Chitinous exoskeleton infused with silicates. Bioluminescent thoracic channels pulsate at 1.4 Hz."
                ),
                SpecimenEntity(
                    specimenCode = "ALPHA-007",
                    commonName = "Alpha Hive Construct",
                    genus = "Xenomorph Prime",
                    originPlanet = "Kepler-16b Basin",
                    status = "MUTATING",
                    dnaSequence = "29GA-XX3X8-13XGT77",
                    aggression = 85,
                    intelligence = 75,
                    venomGlands = true,
                    dermalHardening = 82,
                    bioLuminescence = 90,
                    imageUrl = "https://lh3.googleusercontent.com/aida/ADBb0uiZTlaCg5Wvz_nwytR5G-PBqD-d_3B_rvEF8BApXZ_pS2ksFRV23dh7FAI7A5Kdg3DkkO8vXyMoR6cJqjG4agTSgNfBL05SDljLQ_TSgFFoLZ6nhqQo9tU9vyDb3lhvQaW2EehNTQPK1TgXF6TyuKHdPbc5VemcThVTO9b1Gwto-3A28yMUHHin9e32kMUAT_P1uNe-wR5dh44eHwNNzb7FuKdJkEBWJJ7jFstDd7sW9o6ftwft39K88A",
                    habitatType = "Cryo",
                    notes = "Specimen shows accelerated metabolic splice. Dermal tissue exhibits hyper-conductive thermal properties."
                ),
                SpecimenEntity(
                    specimenCode = "CRYONIX",
                    commonName = "Cryonix",
                    genus = "Reptilian Endotherm",
                    originPlanet = "Triton Cryo-Volcanic Region",
                    status = "STABLE",
                    dnaSequence = "CRYO-449-HELIX-ZERO",
                    aggression = 35,
                    intelligence = 92,
                    venomGlands = false,
                    dermalHardening = 65,
                    bioLuminescence = 74,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDAPesBenVlbqnMDkTxp1wasVaeG2_a-hGEmKqjj_s0LgshUGsmjqAscYhZ9dw-5aaHDZBqvt0avCO4FArxgJztA0X1QEjSsaEXxsTApyCSBbaXmkpKoY6PkewjmvHvJAJEZA1YHaUqA5UoEU11JToIlM36qgYOhTZ8AytAR-GwUFj2f4E2qG8OlyHBEvtuBM2lG1fJHshbuTBHd4TQ-1mEYs-ehlXa-yWRSbFA7SoPflLahOP1DZgxBmEqa7mHs5brcRfsj0KD8ls",
                    habitatType = "Cryo",
                    notes = "Endothermic cold resistance rating 94%. Current divergence index at 89.4% with stealth cloaking organs."
                ),
                SpecimenEntity(
                    specimenCode = "XYLON-7B",
                    commonName = "Xylon-7B",
                    genus = "Insectoid Genus-IV",
                    originPlanet = "Kepler-16b",
                    status = "STABILIZED",
                    dnaSequence = "XYL-7B-NEUR-CHITIN",
                    aggression = 25,
                    intelligence = 55,
                    venomGlands = true,
                    dermalHardening = 40,
                    bioLuminescence = 60,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfCeNF1sZ01ojwOognlX4DOLCnaD7-_sQvrXue2aN7usKmGvXwHSU8VRmOXefeE3Bh3NeCWcUASUtO70dpFp7kMf5MEcH1tCu76PZ_opC-KlrRT0zHbxvKAZgvRuPNyWR_zzOdkpLRqxwMUzJeC79t6aofmoGQQPusAnm9YtGEBBYVvwleF_j1pDWH_B8mVa6_LB4Jisk-RmPvGZo4nsMtcirVggzkO7Z0qlr8yZMgmoOimVRIsgAvaqL-FUU2jx1_LM3qVkJysGk",
                    habitatType = "Bio",
                    notes = "Segmented sensory antennae capable of detecting sub-surface seismic vibrations up to 4 km away."
                ),
                SpecimenEntity(
                    specimenCode = "AEGIS-4B",
                    commonName = "Aegis-4B",
                    genus = "Aquatic Bioluminescent",
                    originPlanet = "Europa Subsurface Ocean",
                    status = "MUTATING",
                    dnaSequence = "AEG-HYDRO-HYPER-90",
                    aggression = 78,
                    intelligence = 88,
                    venomGlands = false,
                    dermalHardening = 92,
                    bioLuminescence = 95,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD2KgXmV9-cDfkGmDyQDOAKAy2GRG_HrWhoexKWg2tTISCuxxcxl-KS9Dcfc3CQCmByy65XZOQfZAEzaQqhVPOFc9gYEXPfvAVHxk6G2VYKmw2jxrh0e2RU0XipNcrJJ0Fr7R0lAcdIQOIQf4billVL2I1GJAWL5zWSApj-hJhWZO5GgSsF814QxC7wLonRGG0zecJN46TlVqONjIKew8YHxNNSdLxbmei1tBL-Uv4M7pWAkxLy9STFXalE5050MCFcbHHgZjrcdjA",
                    habitatType = "Liquid",
                    notes = "Pressure-resistant lipid membrane with luminescent ventral photophores that flash in warning patterns."
                ),
                SpecimenEntity(
                    specimenCode = "AETHER-WING",
                    commonName = "Aether-Wing",
                    genus = "Avian Iridescent",
                    originPlanet = "Zephyria Upper Strata",
                    status = "STABLE",
                    dnaSequence = "AETH-WING-GLIDE-42",
                    aggression = 65,
                    intelligence = 78,
                    venomGlands = false,
                    dermalHardening = 30,
                    bioLuminescence = 80,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDPcu6A0s-MA7VN4bA9HJlNkCly3vo5y7htGmkDwPK6ULpghvDhM-ReHXrpx7UiUp2CCMLb0_XU65IKnu4jepILTjSAeQNSEampdHceJ7D1BbtJLEK-pxW99SGihtSwpseYvSlYZwzYn036inz6JwnNsPD0zLI6FbLaYAbqchUFTF4vvivnhQPxW-ADO7uvXBibINNgR-ilCwRxpYWcDl3q9aYY0FnPjzsNOeOqiKVVv2sWNzdZLEHwxX1-wNXHQ-yCF1YKBLpj00s",
                    habitatType = "Aero",
                    notes = "Lightweight hollow carbon lattice bones and iridescent electromagnetic plumage for tropospheric gliding."
                ),
                SpecimenEntity(
                    specimenCode = "OBSIDION-REX",
                    commonName = "Obsidion-Rex",
                    genus = "Tectonic Quadruped",
                    originPlanet = "Ignis Prime",
                    status = "STABILIZED",
                    dnaSequence = "OBS-TECT-LAVA-BONE",
                    aggression = 55,
                    intelligence = 40,
                    venomGlands = true,
                    dermalHardening = 98,
                    bioLuminescence = 45,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA1jFj4AOndRYDygboYV7VfqQdAdw9jQ790am498VNacNCLGMFa7W4Ep7aa9YRDlCmjonft4cBm29AnfrfV4NuOx5K7JwgMnrbq5LvJXTMps3HiDMeK1sl4p6ovLD7YuiWo517Tzk74oCiZiiyKVSi9z4xvL23J4AgGHJfzalFlnv2wJV40rUGn4BdNRggF_AY60ooO3sp_hPlw9jeDDvKKhtYk_Bbn63vupDXDjjpL0DSkMm9Kl5RsUw-br6N1FbXcDrbqu2kqkXw",
                    habitatType = "Pyro",
                    notes = "Dense basalt carapaces capable of resisting temperatures up to 2400°C. Ventral thermal heat exhaust."
                ),
                SpecimenEntity(
                    specimenCode = "ARCTON-1",
                    commonName = "Arcton-1",
                    genus = "Glacial Amphibian",
                    originPlanet = "Europa Cryo-Ridge",
                    status = "EVOLVING",
                    dnaSequence = "ARCT-1-CRYOSPHERE-GEN2",
                    aggression = 45,
                    intelligence = 65,
                    venomGlands = false,
                    dermalHardening = 55,
                    bioLuminescence = 50,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCjIl_vNSW8pB7XzOkSiWEVvu3vtUVjoY_CP-wl6iCJax2VCh_QbX-e_XCLlQqKgj4586aKyvQI1eYvxIdQt_gTxrJV-sk6P8nirgYy_Z0cNgPQatgWDNKhPJQ1_6nErF2dubmkmRlC1CjdJWCELzVSYIoC598laS1_Kfzn3NPHgJVGeZyQqM1Z1VCdbFcj5_h7Xu-vVPjOAHpBQAg1_xA2hS_y_xWTSUeAUx_603Q_oKydaoXddmgn_NSxye0RV9ncmCzU5rNmjCY",
                    habitatType = "Cryo",
                    notes = "Intermediate ancestral node between single-celled proto-organisms and complex predatory Cryonix."
                )
            )
            specimenDao.insertSpecimens(defaultSpecimens)

            // Seed initial logs
            val defaultLogs = listOf(
                MutationLogEntity(
                    specimenCode = "ALPHA-007",
                    message = "Wing tissue grafted onto dorsal nodes.",
                    timeLabel = "14:02:45",
                    tag = "MUTATION"
                ),
                MutationLogEntity(
                    specimenCode = "SPECIMEN-X9",
                    message = "Pigment shift to #00DBE9 bioluminescent band.",
                    timeLabel = "13:58:12",
                    tag = "INFO"
                ),
                MutationLogEntity(
                    specimenCode = "SPECIMEN-X9",
                    message = "Venom glands active. Neurotoxin synthesis initiated.",
                    timeLabel = "13:45:00",
                    tag = "ALERT"
                ),
                MutationLogEntity(
                    specimenCode = "ALPHA-007",
                    message = "Splice sequence Alpha-Prime integrated successfully.",
                    timeLabel = "12:15:30",
                    tag = "SPLICED"
                ),
                MutationLogEntity(
                    specimenCode = "CRYONIX",
                    message = "Glacial cloaking efficiency recalibrated to 94%.",
                    timeLabel = "11:04:18",
                    tag = "INFO"
                )
            )
            defaultLogs.forEach { specimenDao.insertMutationLog(it) }
        }
    }

    suspend fun addMutationLog(specimenCode: String, message: String, tag: String = "MUTATION") {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val log = MutationLogEntity(
            specimenCode = specimenCode,
            message = message,
            timeLabel = sdf.format(Date()),
            tag = tag
        )
        specimenDao.insertMutationLog(log)
    }

    suspend fun updateSpecimenModifiers(
        specimenCode: String,
        aggression: Int,
        bioLuminescence: Int,
        dermalHardening: Int
    ) {
        specimenDao.updateModifiers(specimenCode, aggression, bioLuminescence, dermalHardening)
        addMutationLog(
            specimenCode = specimenCode,
            message = "Modifiers altered: Aggression $aggression%, Luminescence $bioLuminescence%, Dermal $dermalHardening%.",
            tag = "MUTATION"
        )
    }

    suspend fun updateSpecimen(specimen: SpecimenEntity) {
        specimenDao.updateSpecimen(specimen)
    }

    suspend fun addNewSpecimen(specimen: SpecimenEntity): Long {
        return specimenDao.insertSpecimen(specimen)
    }

    suspend fun deleteSpecimen(id: Int) {
        specimenDao.deleteSpecimenById(id)
    }
}

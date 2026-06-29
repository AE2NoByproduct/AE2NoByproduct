import gg.meza.stonecraft.mod

plugins {
    id("gg.meza.stonecraft")
}

repositories {
    // Applied Energistics 2 + GuideME, for add-on developers.
    maven("https://modmaven.dev/") {
        name = "Modmaven"
        content {
            includeGroup("appeng")
            includeGroup("org.appliedenergistics")
        }
    }
}

dependencies {
    // Architectury API: loader-agnostic networking, registries and creative tabs. The artifact suffix
    // (forge/fabric/neoforge) matches mod.loader exactly, so the same shared code compiles on each.
    "modImplementation"("dev.architectury:architectury-${mod.loader}:${mod.prop("architectury_version")}")

    // Applied Energistics 2: full mod at compile + dev runtime so the shared appeng.*-targeting mixin
    // resolves. The artifact suffix also matches mod.loader (appliedenergistics2-forge/-fabric/-neoforge).
    "modImplementation"("appeng:appliedenergistics2-${mod.loader}:${mod.prop("ae2_version")}")
    // GuideME is a hard runtime dependency of AE2; not needed to compile our code.
    "modRuntimeOnly"("org.appliedenergistics:guideme:${mod.prop("guideme_version")}")

    if (mod.isFabric) {
        // Team Reborn Energy API: AE2-fabric ships it as a nested jar that loom does not surface on the
        // dev classpath, so AE2 crashes at startup without it. Dev runtime only.
        "modRuntimeOnly"("teamreborn:energy:3.0.0")
    }

    // Plain JUnit unit tests for the loader-agnostic decision logic (EffectiveStateTest).
    "testImplementation"(platform("org.junit:junit-bom:5.10.2"))
    "testImplementation"("org.junit.jupiter:junit-jupiter")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Gradle 9 fails on implicit task dependencies. On Forge, Stonecraft's generatePackMCMetaJson writes
// into build/resources/main, which compileTestJava consumes via the main source-set output, so make
// that ordering explicit. The matcher is empty (no-op) on loaders without the task, e.g. Fabric.
tasks.matching { it.name == "compileTestJava" }.configureEach {
    dependsOn(tasks.matching { it.name == "generatePackMCMetaJson" })
}

modSettings {
    clientOptions {
        narrator = false
    }
}

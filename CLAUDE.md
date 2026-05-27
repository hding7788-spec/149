# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

This repository is a **PTC Windchill 11 (M030) PDM customization** for the "149" project
(a Chinese aerospace/manufacturing deployment). It is not a standalone application — the
compiled output is layered on top of an existing Windchill installation. Almost all source
is Java built with **Apache Ant**, and there is no test suite; correctness is verified by
deploying into a running Windchill MethodServer.

Source comments, readme files, and string resources are largely in Chinese (GBK/UTF-8 mixed).

## Modules

Each top-level directory is an independent module with its own build:

- **PDM/** — the core server-side customization (3000+ Java files). Built by `build_149.xml`.
  Compiles directly into `$WT_HOME/codebase` and ships config, SQL, load files, and resources.
- **ServiceWeb1.03/** — web-service / print integration customization (`com.glaway.mpm.*`).
  Built by `build.xml`, same Windchill-shell pattern as PDM.
- **Pbom1.03/** — process BOM builder (`com.glaway.mpm.pbom*`). Source only.
- **QMEditor1.6/** — a standalone **Swing desktop tool** (capp/technics editor), packaged as a
  runnable jar via `build.xml`. Unlike the others it does *not* deploy into Windchill; it
  targets Java 1.6 and bundles its own `lib/`. Note the hard-coded `dest.dir`/`jar.file.name`
  Windows paths in `QMEditor1.6/build.xml` — adjust before building.
- **dc_rabbit-common/** — RabbitMQ-based messaging/sync integration between Windchill domains
  (`com.bjsasc.avidm.mq.*`, event/message types per peer system: a4, a5, dc, site, win10, win11).
- **speWord/** — Word-document special-symbol / template generation tool (`com.glaway.speciaword.*`).

Only PDM, QMEditor1.6, and ServiceWeb1.03 are registered as IntelliJ modules (`.idea/modules.xml`);
PDM depends on QMEditor1.6. The IDE classpath expects an external `Windchill11M030.jar`.

## Building

The PDM and ServiceWeb builds **must run inside a Windchill Shell** with `WT_HOME` set —
`build.xml`'s `check_path` target fails otherwise. They write artifacts straight into the
live Windchill install (`$WT_HOME/codebase`, `/db`, `/conf`, `/loadFiles`, ...), so treat a
build as a deployment step, not a local compile.

```sh
# from PDM/ or ServiceWeb1.03/, inside a Windchill Shell:
ant -f build_149.xml            # PDM full build (filecopy + javagen + javac + resourcebuild + sqlgen)
ant -f build_149.xml jc         # compile Java only
ant -f build_149.xml fc         # copy non-Java files only
ant -f build_149.xml jg         # JavaGen + modelInstall for persistable model classes
ant -f build_149.xml rb         # build .rbInfo resource bundles (ResourceBuild)
ant -f build_149.xml sqlgen     # generate SQL DDL from ext.ases.* model classes
```

`build_149.xml`'s `i_javac` compiles a **curated include list** (`com/ptc/extend/**`,
`ext/casc/**`, `ext/ases/**`, `ext/sast/**`, `ext/csc/**`, plus a handful of named OOTB
overrides) and explicitly **excludes** several files. When adding a new top-level package or
a class that overrides a PTC class, update the `<include>`/`<exclude>` lists accordingly.

QMEditor is a normal Ant jar build (no WT_HOME): `ant -f QMEditor1.6/build.xml jar`.

## Deployment pipeline (the "real" workflow)

`PDM/readme.txt` is the authoritative, step-by-step deploy runbook (in Chinese). The recurring
operations a change may require:

- **Java model objects** (persistable / `*Link` classes under `ext/ases/...`): run `jg`
  (JavaGen + modelInstall) so the schema/introspection is registered, then `sqlgen` for DDL.
- **Service / DataUtility / TreeHandler registration**: edit the `.xconf` files under
  `PDM/codebase/ext/casc/conf/` (`casc_149.xconf`, `casc_datautility.xconf`) and apply with
  `xconfmanager -i <file> -p`. These merge entries into Windchill `.properties` files.
- **Custom Windchill services** follow the OOTB Manager pattern: a `*Service` interface +
  `Standard*Service extends wt.services.StandardManager`, registered via `.xconf` and started
  on MethodServer boot (see `ext/casc/service/StandardCascCacheService.java`).
- **Registry additions** (`PDM/codebase/*.properties.addition` —
  `associationRegistry`, `descendentRegistry`, `modelRegistry`): must be **manually merged**
  into the corresponding `$WT_HOME/codebase` files; the build does not merge them.
- **Resource bundles** (`.rbInfo`): rebuild with `ResourceBuild <bundle> true -Dbundle.forceCompile=true`
  (the `rb` target lists the project's bundles).
- **UI actions**: action/action-model XML under `PDM/codebase/.../*-actions.xml` and
  `*-actionModels.xml` (PTC pattern); `PDM/ootbActions/` holds reference copies of the OOTB action files.
- **Load files / type definitions**: `windchill wt.load.LoadFileSet -file loadSet.xml ...`.
- **SQL**: scripts live under `PDM/db/`; run with SQL*Plus as the Oracle schema owner.
- **Preferences & security labels**: several Windchill Preference toggles and the
  `securityLabelsConfiguration_149.xml` steps are in `readme.txt` and `149安全标签配置步骤.txt`.

## Source-tree conventions

Customization code lives under a few stable namespace roots — know which to extend:

- `ext.casc.*` — the bulk of 149 customization, organized by domain (`change`, `workflow`,
  `process`, `mpm`, `report`, `lifecycle`, `access`, `securitymgr`, `dataUtility`, `service`, ...).
- `ext.ases.*` — modeled business objects (envelope/signature/technotice/changepackaged);
  these are the classes that go through JavaGen and SQL generation.
- `ext.sast.*`, `ext.csc.*` — additional customization (catalog/supply/center, utilities).
- `com.glaway.*` — Glaway-authored tools (mpm/pbom, security beans, speciaword).
- `com.bjsasc.avidm.mq.*` — RabbitMQ messaging integration (dc_rabbit-common).
- `com.ptc.extend.*` and selected `com/ptc/...` / `wt/...` paths — overrides of OOTB PTC classes
  (compiled over the shipped ones; treat with care).

`mvc/builder` subpackages implement PTC's MVC component-builder pattern for table/info UIs;
`datautility` classes feed column values into those tables and are wired through `.xconf`.

## Conventions & gotchas

- **Encoding**: Ant build files declare `encoding="GBK"`; Java is compiled with `utf-8`/UTF-8.
  Many source files and all readmes contain Chinese text — preserve existing encodings when editing.
- There are **no automated tests and no CI**; `ext/casc/test` and `ext/test` are ad-hoc mains.
- The build has empty `backup`/`clean`/`i_backup` targets — builds overwrite the live Windchill
  install in place. There is no rollback; back up `$WT_HOME` externally before a full build.
- The `.idea/` project files and `.iml` module configs are committed and assume specific local
  JARs/paths; they are for reference, not portable.

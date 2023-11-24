## Release process

This documentation focused on release process for Advanced Peripherals.

### Preparation

First, you need to get curseforge api
key. [Here](https://support.curseforge.com/en/support/solutions/articles/9000197321-curseforge-api) is some
documentation about it.

Then, you need to get modrinth key, from [settings](https://modrinth.com/dashboard/settings) in "Authorization token"
section

Second, you need to create `.env` file in root folder and put in this file

```
CURSEFORGE_KEY=<your_api_key>
MODRINTH_KEY=<your_api_key>
```

If you also want to do manual upload to [AdvancedPeripheral maven repository](https://mvn.intelligence-modding.de/), you can add next variables

```
PUBLIC_REP_NAME=<Reposilite Username>
PUBLIC_REP_KEY=<Reposilite Access Token>
```

### Before release

- Manually review `CHANGELOG.md` to check it for mistakes or some problems
- Make sure, that `mod_version` and `release_type` in `gradle.properties` matches to what you want to receive

### Release process

- Execute `./gradlew patchChangelog` or manually update `CHANGELOG.md` and commit result to repository
- Run `git tag <minecraft_version>-<version>` to create git tag and push it to repository via `git push --tags`
- Upload project to curseforge via `./gradlew curseforge`
- Upload project to modrinth via `./gradlew publishModrinth`
- Update `mod_version` to next patch or minor version, what you want

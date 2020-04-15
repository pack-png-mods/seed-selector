# seed-selector
Allows you to select your world seed and spawn point in Alpha 1.2.2a

## Installation
1. Download and install [MultiMC](https://multimc.org/#Download) if you haven't already.
1. If you haven't already, press "create instance", and press "import from zip", and paste the following URL into the text field: https://cdn.discordapp.com/attachments/666758878813487136/699323306637262928/fabric-alpha.zip
1. Download seed-selector from the [releases page](https://github.com/pack-png-mods/seed-selector/releases).
1. Click on your new MultiMC instance and click "edit instance" on the right. Click "loader mods" then "add", and navigate to the mod you just downloaded, and press OK.

## Contributing
1. Clone the repository
   ```
   git clone https://github.com/pack-png-mods/seed-selector
   cd seed-selector
   ```
1. Generate the Minecraft source code
   ```
   ./gradlew genSources
   ```
   - Note: on Windows, use `gradlew` rather than `./gradlew`.
1. Generate the IDE project depending on which IDE you prefer
   ```
   ./gradlew idea      # For IntelliJ IDEA
   ./gradlew eclipse   # For Eclipse
   ```
1. Import the project in your IDE and edit the code
1. After testing in the IDE, build a JAR to test whether it works outside the IDE too
   ```
   ./gradlew build
   ```
   The mod JAR may be found in the `build/libs` directory
1. [Create a pull request](https://help.github.com/en/articles/creating-a-pull-request)
   so that your changes can be integrated into seed-selector
   - Note: for large contributions, create an issue before doing all that
     work, to ask whether your pull request is likely to be accepted

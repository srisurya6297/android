## Start contributing :hammer_and_wrench:
Make sure you read [SETUP.md](https://github.com/srisurya6297/android/blob/master/SETUP.md) and [CONTRIBUTING.md](https://github.com/nextcloud/android/blob/master/CONTRIBUTING.md) before you start working on this project. But basically: fork this repository and contribute back using pull requests to the master branch.
Easy starting points are also reviewing [pull requests](https://github.com/nextcloud/android/pulls) and working on [starter issues](https://github.com/nextcloud/android/issues?q=is%3Aopen+is%3Aissue+label%3A%22starter+issue%22).

### Getting debug info via logcat :mag:
#### With a computer:
- connect the device via USB
- open command prompt/terminal
- enter `adb logcat | grep "$(adb shell ps | awk '/com.thinkman.client/{print $2}')" > logcatOutput.txt` to save the output to this file

**Note:** You must have [adb](https://developer.android.com/studio/releases/platform-tools.html) installed first!

#### On a device (with root) :wrench:
- open terminal app *(can be enabled in developer options)*
- get root access via "su"
- enter `logcat -d -f /sdcard/logcatOutput.txt`
- you will have to filter the output manually, as above approach is not working on device







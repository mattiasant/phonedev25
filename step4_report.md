#  Step 4 Report —  Polishing + Testing + APK

## Project Title
**5 Papers**

---

## 👥 Team Members

| Name                                                  | Role                                                    |
|-------------------------------------------------------|---------------------------------------------------------|
| Robyn ([Anyro0](https://github.com/Anyro0))           | UI design, project setup, team lead, tester                     |
| Mirko ([mamikek](https://github.com/mamikek))         | Database implementation, data layer integration         |
| Mattias ([mattiasant](https://github.com/mattiasant)) | API calls, QR gen, QR reading, QR decoding, UI elements |

## Testing Strategy
- Performed manual testing on physical Android devices and emulator configurations.
- Unit tests implemented to verify data model integrity.
- UI tests added to confirm that all visual elements are reachable, navigable, and functional across screens.
- Tested core user flows including app launch, navigation, data input, and error handling.
- Verified APK behavior

## Build Process for APK
- Key generated for release signing.
- Added signing configuration into app/build.gradle under the release build type.
- Used Gradle to generate signed APK
- Output APK
- Signature validation confirmed

## Known Bugs or Limitations
- Occasional UI overlap

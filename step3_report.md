#  Step 3 Report — API Integration

## Project Title
**5 Papers**

---

## 👥 Team Members

| Name                                                  | Role                                                    |
|-------------------------------------------------------|---------------------------------------------------------|
| Robyn ([Anyro0](https://github.com/Anyro0))           | UI design, project setup, team lead                     |
| Mirko ([mamikek](https://github.com/mamikek))         | Database implementation, data layer integration         |
| Mattias ([mattiasant](https://github.com/mattiasant)) | API calls, QR gen, QR reading, QR decoding, UI elements |

## 1. API

For Step 3, the project integrates a **public QR generation API** —  [`api.qrserver.com`](https://api.qrserver.com/v1/create-qr-code/).

This API is completely (at the time of creation) **open and keyless**, returning a **PNG image** of a QR code generated from input text.  
Example endpoint used: https://api.qrserver.com/v1/create-qr-code/?data=JoinCode:1234&size=200x200  


**Why chosen:**
- Free and does not require authentication
- Simple RESTful design (`GET` request with parameters)
- Provides an immediate visual result that fits the game’s *join via QR* feature
- Demonstrates integration of live remote data directly into the app’s UI

---

## 2. API Integration Overview

The app uses **Retrofit** for network communication and **Coroutines** for asynchronous calls.  
The QR image returned by the API is displayed dynamically on the **Join Game** screen, allowing users to generate a shareable QR code each time.


## 3. Data Flow Diagram (Conceptual)

User taps “Make QR Code”
↓
ViewModel launches coroutine
↓
Retrofit GET → api.qrserver.com/v1/create-qr-code/
↓
PNG Response → Converted to Bitmap
↓
Displayed in popup dialog (Join_Game screen)

---

## 4. Live Data Display and Error Handling

### Live Data Display
- The QR image is rendered **in a popup dialog** on the *Join Game* screen.
- Each button press generates a **new random join code**, ensuring unique output every time.

### Error Handling
- Network errors are caught with `try/catch` in `QrViewModel`.
- Errors are exposed via a `MutableLiveData<String>` and displayed as **Toast messages**.
- The app shows a **toast** (“Generating QR…”) during the loading state and remains crash-free offline.


## 5. Local vs Remote Processing

| Feature | Source | Description |
|----------|---------|-------------|
| **QR Code Generation** | Remote (Public API) | Uses the `api.qrserver.com` endpoint to create QR images from text. |
| **QR Code Decoding (Scanner)** | Local (On-device ML) | Uses Google’s ML Kit to detect and decode QR codes via CameraX — no internet required. |

---

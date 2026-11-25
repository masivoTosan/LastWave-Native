# LastWave 非官方简体中文测试版

> 本 Fork 由 masivoTosan 维护，用于中文界面测试。原作者为 [Clash-Projects/LastWave-Native](https://github.com/Clash-Projects/LastWave-Native) 及其贡献者；本版本不是官方发行。

**[下载：LastWave 5.2.1 非官方简体中文测试版](https://github.com/masivoTosan/LastWave-Native/releases/tag/5.2.1-zh-test-20261125)**

修改日期：2026-11-25。包含 PR #747 的翻译，并将界面代码中遗漏的英文接入语言资源，新增 1,134 条翻译资源，覆盖弹窗、统计、资料库、设置、小组件和通知。品牌、歌曲、艺人及用户自定义歌单名称保留原文。

| 安装包 | 包名 | 说明 |
| --- | --- | --- |
| 标准汉化测试版 | com.lastwave.app.zhdebug | 推荐，可与正式版共存 |
| vivo-origin 对比版 | com.tencent.qqmusic | 与 QQ音乐冲突；真我 GT6 的额外效果尚未确认 |

两版均为 Android 10+ / ARM64 Debug，APK 内部版本为 5.2.1（33）。已通过语言切换、时间格式测试、编译和签名验证，尚未实机逐页验收。如覆盖旧测试版时签名冲突，请先备份数据后再卸载旧测试版。

本版本继续使用 [GPLv3](LICENSE)，保留上游版权、原始致谢、免责声明和第三方许可。详见 [第三方声明](THIRD_PARTY_NOTICES.md)。源码和构建步骤随 Release 一起提供；APK 对应源码为 [8dea539](https://github.com/masivoTosan/LastWave-Native/tree/8dea5398796363c3f89137a721b0ddf0c10b4381)。标准版独立包名由该提交中的 GitHub Actions 工作流在编译时调整。

发布名称、说明和附件可以在 GitHub Releases 中编辑；修改 APK 内部版本号或程序内容需要重新编译。

## 翻译结构与维护说明

本次修改把原先写在 Kotlin 界面代码中的英文文字接入 Android 字符串资源，不是在界面代码中直接写死中文。原有资源保留在各语言的 strings.xml 中，新增资源集中在：

- 默认英文：app/src/main/res/values/ui_strings.xml
- 简体中文：app/src/main/res/values-zh/ui_strings.xml
- 中国大陆简体中文：app/src/main/res/values-zh-rCN/ui_strings.xml

同一段文字在各语言中使用相同的资源键名。新增语言时，可在对应 values-语言目录下添加相同键名的译文；缺失的新增译文会回退到默认英文。修改现有中文通常只需修改中文资源，并同步两份简体中文文件，保持格式参数一致。

当前测试实现还新增了 util/AppText.kt，供界面、回调和非 Compose 模型统一读取语言资源。它不是上游原有的接口。准备提交上游时，建议单独整理为符合原项目习惯的 stringResource、Context.getString、资源 ID 和数量资源，并审阅资源键名及语言切换行为。

品牌、服务名称、歌曲、艺人、用户自定义歌单名称及程序内部标识应根据语义保留原文。同步上游代码应通过独立分支合并或移植并重新测试；仅修改翻译或应用代码不会自动改变已经发布的 APK，仍需重新构建。


---

## 上游项目原始 README

<div align="center">

<img src="assets/LastWave_banner_v2_1440p.png" alt="LastWave Banner" width="100%" style="border-radius: 16px;" />

<h1>LastWave</h1>

**Simple Android YouTube Music client with Material UI, Liquid Glass, Bit-Perfect playback, Automix, Lossless addons & advanced audiophile features.**

<br/>

<p align="center">
  <a href="https://t.me/clashprojects" target="_blank"><img src="docs/badges/btn_telegram_support.svg" alt="Telegram Support" height="42" /></a>
  <a href="https://t.me/MaterialYouApp" target="_blank"><img src="docs/badges/btn_telegram_channel.svg" alt="Telegram Channel" height="42" /></a>
  <a href="https://discord.gg/DmyM2p2fMe" target="_blank"><img src="docs/badges/btn_discord.svg" alt="Discord Community" height="42" /></a>
</p>

<p align="center">
  <a href="https://trendshift.io/repositories/197973?utm_source=trendshift-badge&amp;utm_medium=badge&amp;utm_campaign=badge-trendshift-197973" target="_blank" rel="noopener noreferrer">
    <img src="https://trendshift.io/api/badge/trendshift/repositories/197973/daily?language=Kotlin" alt="Clash-Projects%2FLastWave-Native | Trendshift" width="250" height="55" />
  </a>
  &nbsp;
  <a href="https://trendshift.io/repositories/197973?utm_source=trendshift-badge&amp;utm_medium=badge&amp;utm_campaign=badge-trendshift-197973" target="_blank" rel="noopener noreferrer">
    <img src="https://trendshift.io/api/badge/trendshift/repositories/197973/weekly?language=Kotlin" alt="Clash-Projects%2FLastWave-Native | Trendshift" width="250" height="55" />
  </a>
</p>
</div>

---




## Overview

LastWave is a fast, native YouTube Music client for Android that looks great and sounds even better. Built with Material 3 Expressive and Liquid Glass UI, it gives you a clean, fluid design that adapts to your music.

For audio lovers, LastWave packs Bit-Perfect USB DAC output, Lossless FLAC addon support, AutoEQ headphone presets with parametric EQ, smooth Automix transitions, Spotify Canvas & integration, and real-time synced lyrics, all in a lightweight app.

---


## Features

<table>
  <tr>
    <td width="50%" valign="top">

#### Playback
- Stream any track or video from YouTube Music
- Background playback with lockscreen & Android Auto controls
- High-res audio downloads with embedded tags & `.lrc` lyrics
- Download & cache for offline listening
- Skip silence & sleep timer

</td>
    <td width="50%" valign="top">

#### Audio
- Bit-Perfect USB DAC output (direct hardware bypass)
- Lossless FLAC addon support (up to 24-bit Hi-Res)
- 15-band Parametric Equalizer & AutoEQ headphone presets
- Smart Automix transitions & smooth crossfade
- Audio normalization (ReplayGain / LUFS)
- Real-time tempo & pitch control

</td>
  </tr>
  <tr>
    <td width="50%" valign="top">

#### Lyrics & Discovery
- Live millisecond-synchronized lyrics (LRCLIB)
- Lyrics romanization for non-Latin scripts (Japanese, Korean, etc.)
- Multi-language lyrics translation
- Spotify Canvas & animated video backgrounds (Apple Music & Tidal)
- Personalized quick picks, mood radios & genre discovery
- Search songs, albums, artists, videos, and playlists

</td>
    <td width="50%" valign="top">

#### Library & Account
- Full library management & offline cached library
- Spotify Integration (Account login, Liked Songs sync & deep links)
- Import public playlists from Spotify & Apple Music
- YouTube Music account login & bidirectional sync
- Local & cloud playlists with custom reordering
- Custom audio tag editing & cover art selection

</td>
  </tr>
  <tr>
    <td width="50%" valign="top">

#### Social
- Listen together with friends in real-time
- Built-in Last.fm scrobbler with background tracking
- Safe Discord Rich Presence with live album art
- Export and share lyrics as aesthetic quote cards

</td>
    <td width="50%" valign="top">

#### Interface
- Material 3 Expressive & Liquid Glass UI
- Real-time backdrop refraction shaders
- Dynamic Monet theming + 19 preset color palettes
- Light, Dark, Pure Black (AMOLED), and Dynamic modes
- Interactive home screen widgets & fluid tactile haptics

</td>
  </tr>
</table>

---



## Getting Started

### Installation

| Channel | Source | Compatibility |
|:---|:---|:---|
| **Stable Release** | [GitHub Releases](https://github.com/Clash-Projects/LastWave-native/releases) | Recommended for all users • Android 7.0+ (API 24+) |
| **Nightly Builds** | [GitHub Actions](https://github.com/Clash-Projects/LastWave-native/actions) | Latest automated builds with bleeding-edge features |
| **Desktop App** | [LastWave-Desktop](https://github.com/Clash-Projects/LastWave-Desktop) | Official desktop client for Windows, macOS & Linux |

### Quick Setup

1. Install the APK on your Android device.
2. *(Optional)* Navigate to **Settings → Audio** to configure:
   - **Bit-Perfect Output:** Enable exclusive USB DAC mode for bit-perfect audio.
   - **AutoEQ Presets:** Search for your headphones/IEMs to apply calibrated parametric EQ curves.
   - **Lossless Addons:** Enable the modular addon service for 24-bit Hi-Res FLAC playback.
3. Start streaming music, listening to customized radios, and enjoying synced lyrics!

---

## Building from Source

### Prerequisites
- **JDK:** OpenJDK 17 or higher
- **Android SDK:** API Level 34+ with CMake & Android NDK installed (for native audio C++ engine & USB driver)

### Build Instructions

```bash
# Clone the repository
git clone https://github.com/Clash-Projects/LastWave-native.git
cd LastWave-native

# Build the release APK
./gradlew assembleRelease

# The generated APK will be at:
# app/build/outputs/apk/release/app-release-unsigned.apk
```

> [!TIP]
> For local development and debugging, run `./gradlew assembleDebug` or install directly to a connected device via `./gradlew installDebug`.

---

## Support the Project

If LastWave has been useful to you, consider supporting its development.

<div align="left">
  <table style="margin: 0 auto; border-collapse: collapse; border: none;">
    <tr>
      <td align="center" style="padding: 15px; border: none;">
        <a href="https://buymeacoffee.com/ajisth" target="_blank" style="text-decoration:none;"><img src="docs/badges/bmac.png" alt="Buy Me A Coffee" width="140" style="border-radius: 12px; box-shadow: 0 8px 16px rgba(0,0,0,0.2);"/></a>
      </td>
      <td align="center" style="padding: 15px; border: none;">
        <a href="https://buymeachai.ezee.li/ajisth69" target="_blank" style="text-decoration:none;"><img src="docs/badges/upi.png" alt="UPI (Buy Me A Chai)" width="140" style="border-radius: 12px; box-shadow: 0 8px 16px rgba(0,0,0,0.2);"/></a>
      </td>
      <td align="center" style="padding: 15px; border: none;">
        <a href="https://github.com/sponsors/ajisth69" target="_blank" style="text-decoration:none;"><img src="docs/badges/github_sponsors.svg" alt="GitHub Sponsors" width="140" style="border-radius: 12px; box-shadow: 0 8px 16px rgba(0,0,0,0.2);"/></a>
      </td>
    </tr>
  </table>
</div>



---


## Acknowledgements

LastWave is made possible thanks to these open-source projects:

- **[Metrolist](https://github.com/MetrolistGroup/Metrolist)** — InnerTubeX & stream resolving
- **[SimpMusic](https://github.com/maxrave-dev/SimpMusic)** — Canvas extraction & Liquid Glass inspiration
- **[InnerTune](https://github.com/z-huang/InnerTune)** — InnerTube extraction architecture
- **[Decent USB Audio Driver](https://github.com/silva96/decent-usb-audio-driver)** — Bit-perfect direct USB DAC audio engine
- **[AutoEQ](https://github.com/jaakkopasanen/AutoEq)** — Headphone & IEM frequency-response calibration targets
- **[NewPipe](https://github.com/TeamNewPipe/NewPipeExtractor)** — Stream extraction & media URL resolving
- **[SponsorBlock](https://sponsor.ajay.app/)** — Video segment detection for music video intro skit synchronization
- **[LRCLIB](https://lrclib.net/)** — Real-time millisecond-synchronized lyrics database
- **[Kyant](https://github.com/kyant0)** — Jetpack Compose backdrop refraction & shape shaders

---

## Contributors

LastWave wouldn't be possible without our incredible community of contributors. Thank you for making it better every day!

<p align="center">
  <a href="https://github.com/Clash-Projects/LastWave-Native/graphs/contributors">
    <img src="https://contrib.rocks/image?repo=Clash-Projects/LastWave-Native" alt="Contributors" />
  </a>
</p>

---


## Disclaimer

LastWave is an open-source, non-commercial application developed strictly for educational and research purposes.

- **No Affiliation:** This project is not affiliated with, funded, authorized, endorsed by, or in any way associated with Google LLC, YouTube, YouTube Music, Spotify, Apple Inc., Last.fm, or any of their subsidiaries.
- **Content Delivery:** LastWave does not host, store on private servers, or distribute copyrighted media. All playback streams and metadata are resolved directly from public endpoints.
- **Trademarks:** All product names, logos, trademarks, and registered trademarks belong to their respective owners.

<!-- [![GitGem](https://gitgem.org/api/badge/github/Clash-Projects/LastWave-Native.svg)](https://gitgem.org/github/Clash-Projects/LastWave-Native) -->
<a href="https://gitgem.org/github/Clash-Projects/LastWave-Native"><img src="https://gitgem.org/api/badge/github/Clash-Projects/LastWave-Native.svg" width="0" height="0" alt="" /></a>

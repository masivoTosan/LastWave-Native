# 第三方声明与来源

本文件为本 Fork 的发布说明补充，不替代任何组件的原始许可证、版权声明或 NOTICE。LastWave 原作者及各第三方作者保留其权利。

- 上游项目：https://github.com/Clash-Projects/LastWave-Native
- 主项目许可证：GNU GPLv3，完整文本见 LICENSE。
- 本次 APK 的源码提交：8dea5398796363c3f89137a721b0ddf0c10b4381
- 修改者：masivoTosan；修改日期：2026-11-25。
- 修改范围：简体中文界面、提示、小组件、通知、本地化资源和测试构建流程。
- 原 README 的致谢、免责声明与商标声明完整保留在本仓库 README 下方。

## 分发组件的源码与许可

| 组件 | 本构建引用版本 | 源码 |
| --- | --- | --- |
| NewPipeExtractor | v0.26.5 | https://github.com/TeamNewPipe/NewPipeExtractor/tree/v0.26.5 |
| InnerTubeX | 0.11.2（源码标签 v0.11.2） | https://github.com/MetrolistGroup/innertubex/tree/v0.11.2 |
| Jellyfin Media3 FFmpeg decoder | 1.2.1+1 | https://github.com/jellyfin/jellyfin-androidx-media/tree/v1.2.1%2B1 |
| FFmpeg（解码器子模块） | 133069b4340961a1b3e8562a0dabfc61b35af84c | https://github.com/FFmpeg/FFmpeg/tree/133069b4340961a1b3e8562a0dabfc61b35af84c |
| AndroidX Media（解码器子模块） | b930b40a16c06318e43c81771fa2b1024bdb3f29 | https://github.com/androidx/media/tree/b930b40a16c06318e43c81771fa2b1024bdb3f29 |
| libsoxr | 0.1.3 | https://github.com/chirlu/soxr/tree/0.1.3 |
| Oboe | 1.10.0 | https://github.com/google/oboe/tree/1.10.0 |
| Decent USB Audio Driver | 随本构建源码提供 | audio/decent-usb-audio-driver/LICENSE（MIT，Copyright © 2026 Marcelo Silva） |

发布附件中的 ThirdPartySources.zip 保存上述外部组件源码归档及原始许可文件。
其他直接及传递依赖的坐标、POM、可取得的源码 JAR、JAR/AAR 内的许可与 NOTICE 由发布流程一并收集到 DependencyMetadata.zip。版本与依赖以构建脚本和该依赖清单为准。没有源码附件的组件不因此被重新授权。

## 上游原始致谢



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


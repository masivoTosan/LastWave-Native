本版本由 masivoTosan 基于 LastWave 编译，为**非官方简体中文 Debug 测试版**，不是上游官方发行，也不是 QQ音乐。

修改日期：2026-11-25。新增 1,134 条翻译资源，覆盖界面、弹窗、提示、小组件和通知；保留品牌、歌曲、艺人和自定义歌单名称。语言切换与中文时间格式测试、两个 APK 构建及签名验证已通过；尚未在手机上逐页验收。

| 文件 | 用途 |
| --- | --- |
| LastWave-zh-standard-debug-arm64.apk | 推荐；包名 com.lastwave.app.zhdebug，可与正式版共存 |
| LastWave-zh-vivo-origin-debug-arm64.apk | 对比测试；包名 com.tencent.qqmusic，与真正的 QQ音乐冲突 |

适用于 Android 10+、ARM64（含真我 GT6）。vivo-origin 在真我 GT6 上的额外效果未确认。
APK 内部版本仍为 5.2.1 / 33。测试签名与官方不同；如与旧测试版签名冲突，请先备份数据再卸载旧测试版。

## 源码、许可与校验

- APK 原始成功构建：https://github.com/masivoTosan/LastWave-Native/actions/runs/45609893772
- APK 精确源码：https://github.com/masivoTosan/LastWave-Native/tree/8dea5398796363c3f89137a721b0ddf0c10b4381
- LastWave-source-8dea539.zip：对应应用源码及构建脚本（包含标准版包名调整步骤）。
- ThirdPartySources.zip：主要开源组件及原生依赖源码、原始许可文件。
- DependencyMetadata.zip：依赖坐标、POM、可取得的源码 JAR 与许可声明。
- LICENSE、USB-driver-LICENSE.txt、THIRD_PARTY_NOTICES.md：主许可证与第三方声明。
- SHA256SUMS.txt：本次下载文件校验值。

本修改版本继续按 GNU GPLv3 分发，保留原作者和第三方版权、许可证及免责声明，不提供担保。各品牌和商标属于其权利人，不代表相关公司认可本项目。

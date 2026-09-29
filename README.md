# 召集之钟：移植

将诡厄巫法（Goety）的召集之钟做成可独立加载的模组，不需要安装 Goety。

## 支持版本

- Minecraft 1.20.1，Forge
- Minecraft 1.21.1，NeoForge

## 用法

在工作台把 8 颗末影珍珠围在钟的周围，合成召集之钟：

```text
末影珍珠  末影珍珠  末影珍珠
末影珍珠    钟      末影珍珠
末影珍珠  末影珍珠  末影珍珠
```

手动敲钟后，会在最多 128 格范围内寻找袭击者。距离钟超过 16 格，或处于遮挡中的袭击者会被传送到钟附近；有自定义名称的生物不会被召集。可在 `config/hookbellport-common.toml` 的 `hook_bell.blacklist` 中填写不受影响的生物 ID。

命令：`/give @p hookbellport:hook_bell`

## 构建

在相应版本分支运行：

```powershell
.\gradlew.bat build
```

分支 `mc-1.20.1` 基于 Forge 1.20.1 模板；分支 `mc-1.21.1` 基于 NeoForge 1.21.1 模板。

## 许可与来源

召集之钟的方块、召集逻辑及外观素材基于 Goety 项目对应实现修改，遵循 MIT 许可。请参见 [LICENSE-Goety.txt](LICENSE-Goety.txt) 和 [NOTICE.md](NOTICE.md)。

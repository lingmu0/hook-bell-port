# 召集之钟：移植 / Hook Bell Port

将诡厄巫法（Goety）的召集之钟做成可独立加载的模组，不需要安装 Goety。

## 支持版本

| Minecraft | 加载器 | 源码分支 | Java |
| --- | --- | --- | --- |
| 1.20.1 | Forge | `mc-1.20.1` | 17 |
| 1.21.1 | NeoForge | `mc-1.21.1` | 21 |
| 26.1.2 | NeoForge | `mc-26.1.2` | 25 |
| 1.20.1 | Fabric | `fabric-1.20.1` | 17 |
| 1.21.1 | Fabric | `fabric-1.21.1` | 21 |
| 26.1.2 | Fabric | `fabric-26.1.2` | 25 |

Fabric 版本需要 Fabric API 和 Fabric Loader 0.19.5 或更新版本。客户端与服务端均需安装对应版本的模组。

## 用法

在工作台把 8 颗末影珍珠围在钟的周围，合成召集之钟：

```text
末影珍珠  末影珍珠  末影珍珠
末影珍珠    钟      末影珍珠
末影珍珠  末影珍珠  末影珍珠
```

手动敲钟后，会在最多 128 格范围内寻找袭击者。距离钟超过 16 格，或处于遮挡中的袭击者会在约 2 秒后被传送到钟附近；有自定义名称的生物不会被召集。钟附近需要有可站立的空间。

命令：`/give @p hookbellport:hook_bell`

## 配置

Forge / NeoForge：`config/hookbellport-common.toml` 的 `hook_bell.blacklist`。

Fabric：首次启动生成 `config/hookbellport-common.json`。填写不受影响的生物 ID，修改后重启游戏或服务器：

```json
{
  "hook_bell": {
    "blacklist": ["minecraft:witch"]
  }
}
```

## 构建

切换到相应分支，使用对应 JDK 运行 `./gradlew build`（Windows：`.\gradlew.bat build`）。构建产物位于 `build/libs`，发布时使用不含 `-sources` 或 `-dev` 后缀的 jar。

## 许可与来源

召集之钟的方块、召集逻辑及外观素材基于 Goety 项目对应实现修改，遵循 MIT 许可。请参见 [LICENSE-Goety.txt](LICENSE-Goety.txt) 和 [NOTICE.md](NOTICE.md)。

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/hook-bell-port) · [源码](https://github.com/lingmu0/hook-bell-port)

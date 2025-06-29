# Time-Feeds-Villager
Time Feeds: Villager (逝者如饲：村民)

Dependency: [Mixin Extras](https://repo1.maven.org/maven2/io/github/llamalad7/mixinextras-forge/0.4.1/mixinextras-forge-0.4.1.jar)

一、死亡
- [x] 村民成年后会在现实时间 2 小时后死亡（可配置时间）。

二、饥饿
- [x] 村民每隔现实时间 10 分钟就会进入饥饿状态，并主动寻找一次食物，饥饿状态的村民无法交易（可配置饥饿状态间隔时间）。
- [x] 村民进食时直接将食物拿在手上，与原版繁殖机制区分开。
- [x] 玩家无法与饥饿状态的村民交易，但如果交易中的村民不会进入饥饿状态。
- [x] 睡眠状态下的村民停止饥饿倒计时。

三、金苹果
- [x] 手持金苹果或附魔金苹果右键村民，可以使该村民永不因衰老而死亡（可配置喂食物品）。

四、皮肤
- [x] 村民拥有更多初始皮肤。
- [x] 玩家可以通过 UI 设置村民皮肤。

五、繁殖
- [x] 两位村民必须都处于“停留模式”（见 6-3）才可以繁殖。

六、额外物品栏
- [x] 对村民进行下蹲右键可以打开村民的额外物品栏。额外物品栏有八格，玩家可以放置和拿取物品。
- [x] 村民饥饿时会主动消耗物品栏中的物品，如果物品栏中没有食物，则开始寻找食物。
- [x] 玩家可以设置村民行为模式，包括“工作模式”“停留模式”。
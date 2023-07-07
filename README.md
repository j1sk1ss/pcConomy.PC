
![Alt Text](https://github.com/j1sk1ss/pcConomy.PC/blob/master/cover1.png)
# PcConomy plugin
## Main info:
This is a plugin for minecraft [1.20.1](https://www.minecraft.net/ru-ru/updates/trails-and-tales) version. 

A few words about: This plugin realise 
market economy in minecraft. And yes - it`s all... But let me more explain
how he works in next part of **ReadMe**.

----------------------------------------
### Needed stuff:

>> XConomy
> 
>> Vault
>
>> Citizens
>
>> LuckPerms
>
>> Denizen
>
>> Towny
----------------------------------------
## How economy works:
![Alt Text](https://github.com/j1sk1ss/pcConomy.PC/blob/master/cover2.png)

In few words: we have bank, town and player objects. And any of them can do many things. For example:

----------------------------------------
**Player actions list**
- Take loan from bank (NPC) loaner
- Take loan from Player (Player`s town) loaner
- Deposit cash into bank
- Withdraw cash from bank
- Rent traders and sell resources in Players`s town *Needs license*
- Create traders in own town (Set price of trader and margin) *Needs license*
- Create loaners in own town *Needs license*
- Buy resources from ranted traders
- Buy and sell resources in NPC towns
----------------------------------------
**NPC town actions list**
- Dynamically change resource price
- Create resources
- Use resources
----------------------------------------
**Bank actions list**
- Change taxes
- Change VAT
- Change maximum of loan
- Give loans
- Change maximum of available deposit
- Sell licenses
----------------------------------------
## More about AFK trading:
![Alt Text](https://github.com/j1sk1ss/pcConomy.PC/blob/master/cover3.png)

Every player in their towns (*Towny*) can place a **Trader** and set a town margin with rent trader cost per day. 

U can do it by using next command to enter **mayor menu**
>> /town_menu

And press **Create trader** (Requires *license*)

After all steps u can set rant price, town margin and leave trader for finding client. I mean, every people may have interest to visit ur town cuz ur AFK traders have friendly rent price and low town margin. 

----------------------------------------
## Useful commands:

| Command             | Action                               |
|---------------------|--------------------------------------|
| /create_banker      | Creates banker npc                   |
| /create_loaner      | Creates loaner npc                   |
| /create_npc_loaner  | Creates NPC loaner npc               |
| /create_trader      | Creates trader npc                   |
| /create_npc_trader  | Creates NPC trader npc               |
| /create_licensor    | Creates licensor npc                 |
| /take_cash [Amount]         | Take cash from player`s inventory    |
| /create_cash [Amount]        | Create cash                          |
| /reload_towns       | Reload and save towns                |
| /save_data          | Save plugin data                     |
| /switch_town_to_npc [Town name] | Switch player`s town into NPC town end back   |
| /town_menu          | Open mayor menu                      |
| /add_trade_to_town [Town name] [Resource] [Amount] | Add new trade resource into NPC town |
| /reload_npc         | Reload and save NPC                  |
| /full_info          | Get all info of plugin               |

----------------------------------------
## How to rent AFK trader and sell resources?

After buying a trader in player`s town (or using ur own trader in ur own town) u can put up for sale any resource what u want and set a price what u want, but remember that a price will have a surcharge in the form of VAT and the city's proxy.

After this u can go and take profit from trader.


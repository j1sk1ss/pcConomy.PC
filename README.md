
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
- Sell town shares
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

| Command                                            | Action                                      |
|----------------------------------------------------|---------------------------------------------|
| /create_banker                                     | Creates banker npc                          |
| /create_loaner                                     | Creates loaner npc                          |
| /create_npc_loaner                                 | Creates NPC loaner npc                      |
| /create_trader                                     | Creates trader npc                          |
| /create_npc_trader                                 | Creates NPC trader npc                      |
| /create_licensor                                   | Creates licensor npc                        |
| /take_cash [Amount]                                | Take cash from player`s inventory           |
| /create_cash [Amount]                              | Create cash                                 |
| /put_cash2bank [Amount]                            | Put cash from inventory to bank             |
| /reload_towns                                      | Reload and save towns                       |
| /save_data                                         | Save plugin data                            |
| /switch_town2npc                                   | Switch player`s town into NPC town          |
| /switch_town2player                                | Switch npc`s town into player town          |
| /town_menu                                         | Open mayor menu                             |
| /add_trade2town [Town name] [Resource] [Amount]    | Add new trade resource into NPC town        |
| /reload_npc                                        | Reload and save NPC                         |
| /full_info                                         | Get all info of plugin                      |
| /transfer_share [Town name] [Count] [New owner]    | Transfer ownership of share                 |
| /shares_rate                                       | Shares prices                               |
| /global_market_prices                              | Market prices between NPC traders           |

----------------------------------------
## Player actions list explaining:

1) Almost every player (This is the player who played a certain number of days on the server and was not seen in "dark deeds" (Roughly speaking, does not belong to a city in which there is a malicious defaulter)) can borrow. 
   1) Interest, daily payment and term are selected directly from the creditor. They can be either the NPS bank or the player's city.
   2) Payments are made every game day, and if there is no money in your bank account, then the overdue payment is recorded in your credit history. As you understand, a bad credit history will spoil not only your future life, but also the life of all your fellow countrymen.
2) The bank makes it possible to both withdraw and deposit your savings in the form of paper currency. The advantages of storage can be called the safety of funds. The disadvantages include not full control. How is that not complete control? Every game day, the bank updates the daily budget for the issuance of funds. In other words, the bank will not be able to give out your huge bank account in paper form, but if many people deposit their savings in the bank on this day, maybe you will be lucky and you will be approved of your operation.
3) The basis of the economy of this plugin can be called merchants and creditors. Let's go in order.
   1) The merchant is an NPS that acts as a replacement for the auction familiar to all users (AFK trading). Now there is no global market, there are only merchants scattered across different cities that can be rented by random travelers to sell their products. Renting a merchant is simple - for this you need to obtain an appropriate license, earn money for a down payment, familiarize yourself with the terms of the interest rate in the city, and rent an NPS. You can read more about the interest rate above.
   2) Lender - the same NPS that you can buy and install in your city to do business. By setting its limit on the budget available for issuing loans, you can safely leave and wait for your profit. But also do not forget that without access to a global credit history, your NPS will issue loans to anyone.
4) Each NPC city can have a merchant NPC (It is advisable to set it to the server administrator). With the help of this merchant, random travelers will be able to both earn extra money and buy goods. But the only thing that distinguishes such a merchant from others: He can refuse the transaction for several reasons (Too expensive and too few goods in stock)
5) Each city (In the future this list will be reduced to cities with a special form of government) has the opportunity to create and expose their shares on the global market. So the owner of the city, having chosen the percentage of the property that he wants to sell, the number of shares, the type of share (Pay interest or not) and the price of the share can enter the share market. After that, anyone who buys shares will receive dividends (for the corresponding type of shares) and will have the opportunity to resell. (More will be described in the dev blog)

----------------------------------------
## NPC town actions list explaining:

1) Each NPC city produces and consumes resources from which the internal market value is formed, at which, in fact, random players will trade with NPC merchants.

----------------------------------------
## Bank actions list explaining:

1) The bank in this plugin has a fairly large impact on the entire economy. This influence consists both in the impact on the global credit history (it is the bank that determines for the whole world whether a loan on the terms that the player has chosen is safe for the lender), in the impact on VAT (VAT is involved in almost all transactions from the purchase of NPS to the sale of shares) and influencing the withdrawal limit. All these values change depending on the global economic situation. The bank, in other words, will try to maximize its profits by increasing VAT, lowering the entry threshold for lending, reducing the withdrawal limit if it notices that its income (VAT, loans, invested funds) will decrease.
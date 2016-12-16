# DistributedTransaction
Distribution Transaction Processing across Micro - Services. 

Goal - 

Successfully process transactions which are spread across multiple microservices/servers. 

Example - 

1. Suppose you are ordering/purchasing an item from a website. So this order is successfully completed if Payment is successful
   and Orders are properly placed and the inventory is Updated within the given timeline. 

2. If the Payment Service and Order Service and Inventory Service are present under same application then 
   Transaction Management is easy and manageable.

3. But if the Payment Service aend Order Service and Inventory Service are different microservices, then the transaction
   management becomes painful because all are lying under different JVMs/servers. Also any faiulre happened in any
   microservice should rollback the inserts/updates/deletes done by other microservices taking part in that particular
   transaction.

4. This application will be able to get you through the basics of the transaction processing between two microservices which 
   can be applied to as many microservices you have. 

Technology Used 

1. Spring Boot
2. JMS
3. Atomikos Transaction Manager
4. MySQL
5. XA Transactions using 2 Phase commit. 
6. JTA Transaction Management
7. Java 8
8. Active MQ

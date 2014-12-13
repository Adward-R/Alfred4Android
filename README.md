Alfred4Android
==============

**Project of Intelligent Terminal Course**


*2014.12.3 COLLIMATOR UPLOADED*

 - An open source Android Application under Apache License 2.0 as our reference of developing new search APP
 
 - If the uploaded version has some problem please turn to
      

	https://code.google.com/p/collimator/source/checkout
     
    
 or run the following command in terminal:
     
     
	svn checkout http://collimator.googlecode.com/svn/trunk/ collimator-read-only
	
	
**Remaining Crucial Bugs in Demos**

 - 删除完AppSearchActivity中的搜索关键字试图回到（重新用intent开启一个并重绘）SearchActivity时APP意外退出，猜想是initView时getIntent()得到的东西有问题……解决这个问题似乎有些复杂，试试绕过？（已解决，但是搜索框中关键字拷贝不了）
 
 - APP搜索结果超过一页时最下面的结果统计栏就显示不出来了，考虑换一种布局，使它一直pin在低端，线性布局似乎要出问题；
  
 - 最上面的ActivityLabel很丑啊为什么去不掉……不设置它就会显示成包名orz；
 
 - 自己写的Activity跑起来以后界面为啥不是透明的（明明xml里面设成了透明）！是不是被底下没有finish的原活动影响了…… 
 
 - 微信电话本这个叼玩意儿！好像添加了联系人以后retrieve不到啊……
 
 - 短信还没做，挖个小坑，还有幽灵null contact的问题；
 
 - 联系人搜索效率太低了；
  
     
    
     

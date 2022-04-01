# Peace
a program I wrote in JAVA to decode the financial markets. Took me hundreds of hours, and it works. Enjoy!

The code uses a technique, called cross-validation, that compares simulated profits derived via buying and selling according to what are called 'technical indicators'. These indicators are calculated by looking at certain patterns in metrics (for instance, the change in volume vs. price over a span of ten days) and I used an artificial intelligence tool called evolutionary computation to slightly modify the indicators to get more accurate prediction models. 

Peace picks the best rules by combining a bunch of indicators, then buying and selling according to those rules and calcuating the final profit vs. the initial investment. The 'fitness' of those rules are then calcuated by a formula that I wrote, and the rules that meet or exceed that fitness are saved for comparison against data that the program hasn't encountered yet. If the rules derive a profit on data it hasn't 'seen', in other words if it uses 0% of the data to calculate the best rule and then trades on the remaining 20% of that data, and those later trades derive a profit, I save that rule as a good one. 

I incorporated what's called 'stochastic analysis', which means I introduced randomness into the calculations to subvert overfitting. Overfitting is when a program's decision mechanism adheres so closely to its given data that it can't accommodate any unpredicted behavior, which in a system as complex as the stock market is a virtual certainty. To accommodate for that stochastic behavior, I divided the data into random sized 'folds', and then calculated a confidence factor based on how many of those folds the data produces a profit. So, if I had a dataset that had 20,000 days of trading history, and divided that data into 20 folds, to reach a confidence level greater than 90%, the trading rule Peace calculated will have to derive a profit in 18 or more folds because 18/20 = 90% 


That's a lot of technical jargon to say that, the code makes rules that it can then test. It then sorts the best rules according to how good they are, and stores them for future reference. 

To get Peace to work, put the files in a directory and run teh makefile.  

I've included all the jar files (they should be in the lib folder one level abstracted in the src folder you need and a big chunk of the rules from the larger stocks (which tend to have more predictbale behavior given that they have a larger volume of shares traded and are therefore less subject to volatility), and will upload rules from smaller stocks a little later.

I'm working on a front end so you can install an app and trade on the tips more easily, but for now that's where I am. 
  

I've uploaded some rules, called F_ruleList, that I've generated over the span of around 3000 runs of the program. The file size is pretty big, to decode that data you can look at the code near the end of the Peace file to see how it's appending the data to the preexisting rule set. I changed the way I stored the data a few times while running it, so please undestand that different rule data is the result of different runs. Needless to say, there are plenty of rules and if you compare the newer data to the older you can see what I've added or removed in the newer rule sets.  

IF you wanna incorporate newer data, check out Google Finance, Yahoo, the WSJ, Bloomberg, Quandl, or any of the other sources (like your local newspaper), that provide stock quotes.  

Here is a website that has a lot of good explanations on the formulae involved in creating the indicators if you wanna add more to the decision tree (you should adjust the branching factor if you do add more to allow for slightly larger decision trees): 

_http://www.fmlabs.com/reference/default.htm_

Generating more rules may help predict the fluctuations in times of lesser or greater volatility, and adding additional indicators will help accommodate those fluctuations as well. I'm working on a nnother script that incorporates those, so look for that in the near term too!

Every stock I've used my algoriothm on has proven to be reliably effective in generating rules that are consistent with market fluctuations, but as I said: the best (most reliable) are generated from running the program on stock data from larger corporations. Not absolutely, but of the DJ and larger NASDAQ stocks, >90%, follow that plan. 

I recommend reading up on technical indicators and learning a bit on how the program's infrastructure works in general. Things like genetic algorithms, decision trees, and other evolutionary computation related topics are important to this program, and I've incorporated them because they work. 

I wish to emphasize that the program is dependent upon a library that calculates the technical indicators that I did not write, but whose license may be found here, and in the set of files I've included in the repository, per the author's instructions. If you wish to write your own, please see the formula on the webpage listed on line 23, or see the .xls books on the xls repository. 

The Apache license required for the various jars except the technical indicators is included in the joda-apache-license.text file.

Here is the license from the technical indicator library incorporated in Peace: 

https://github.com/mdeverdelhan/ta4j and reads as follows:

The MIT License (MIT)

Copyright (c) 2014-2016 Marc de Verdelhan & respective authors (see AUTHORS)

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



Peace 

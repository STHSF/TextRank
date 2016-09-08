## 关于自动文摘

利用计算机将大量的文本进行处理，产生简洁、精炼内容的过程就是文本摘要，人们可通过阅读摘要来把握文本主要内容，这不仅大大节省时间，更提高阅读效率。但人工摘要耗时又耗力，已不能满足日益增长的信息需求，因此借助计算机进行文本处理的自动文摘应运而生。近年来，自动文摘、信息检索、信息过滤、机器识别、等研究已成为了人们关注的热点。

**自动文摘**（Automatic Summarization）的方法主要有两种：**Extraction**和**Abstraction**。

其中Extraction是抽取式自动文摘方法，通过提取文档中已存在的关键词，句子形成摘要；Abstraction是生成式自动文摘方法，通过建立抽象的语意表示，使用自然语言生成技术，形成摘要。由于生成式自动摘要方法需要复杂的自然语言理解和生成技术支持，应用领域受限。所以本人学习的也是抽取式的自动文摘方法。

目前主要方法有：

* **基于统计：** 统计词频，位置等信息，计算句子权值，再简选取权值高的句子作为文摘，特点：简单易用，但对词句的使用大多仅停留在表面信息。

* **基于图模型：** 构建拓扑结构图，对词句进行排序。例如，TextRank/LexRank

* **基于潜在语义：** 使用主题模型，挖掘词句隐藏信息。例如，采用LDA，HMM

* **基于整数规划：** 将文摘问题转为整数线性规划，求全局最优解。

## TextRank算法

TextRank 算法是一种用于文本的基于图的排序算法。其基本思想来源于谷歌的 PageRank算法, 通过把文本分割成若干组成单元(单词、句子)并建立图模型, 利用投票机制对文本中的重要成分进行排序, 仅利用单篇文档本身的信息即可实现关键词提取、文摘。和 LDA、HMM 等模型不同, TextRank不需要事先对多篇文档进行学习训练, 因其简洁有效而得到广泛应用。

最早提出[TextRank](http://web.eecs.umich.edu/~mihalcea/papers/mihalcea.emnlp04.pdf)的论文是：

Mihalcea R, Tarau P. TextRank: Bringing order into texts[C]. Association for Computational Linguistics, 2004.

我们先从PageRank讲起。

### PageRank

PageRank最开始用来计算网页的重要性。整个www可以看作一张有向图图，节点是网页。如果网页A存在到网页B的链接，那么有一条从网页A指向网页B的有向边。

构造完图后，使用下面的公式：
![pr值计算公式](http://od6jpuxvb.bkt.clouddn.com/u6jaIzY.png)
S(Vi)是网页i的中重要性（PR值）。d是阻尼系数，一般设置为0.85。In(Vi)是存在指向网页i的链接的网页集合。Out(Vj)是网页j中的链接存在的链接指向的网页的集合。|Out(Vj)|是集合中元素的个数。

PageRank需要使用上面的公式多次迭代才能得到结果。初始时，可以设置每个网页的重要性为1。上面公式等号左边计算的结果是迭代后网页i的PR值，等号右边用到的PR值全是迭代前的。

### 使用TextRank提取关键字

关键词抽取的任务就是从一段给定的文本中自动抽取出若干有意义的词语或词组。将原文本拆分为句子，在每个句子中过滤掉停用词（可选），并只保留指定词性的单词（可选）。由此可以得到句子的集合和单词的集合。TextRank算法是利用局部词汇之间关系（共现窗口）对后续关键词进行排序，直接从文本本身抽取。其主要步骤如下：

1、把给定的文本T按照完整句子进行分割，即

![](http://od6jpuxvb.bkt.clouddn.com/GJJp0dC+Iu3nuQnt95RllXzy3V5AAAAABJRU5ErkJggg==.png)

  2、对于每个句子![](http://od6jpuxvb.bkt.clouddn.com/YtRBAzUQPjHVjQv3z50sjICMhubGwk1kF1YIBVaUZGBjBnYYp7gAEuCyBg9uzZ+BXgdBAecPHiRScnJ+LVkwdIc1BRURHtnAIBpDkImLrnzZtHO9cwkOSgFy9e0M4dcDDoGmgA9CATy1d+XhEAAAAASUVORK5CYII=.png)，进行分词和词性标注处理，并过滤掉停用词，只保留指定词性的单词，如名词、动词、形容词，即![](http://od6jpuxvb.bkt.clouddn.com/wdRhSHL8S992WFn2WXCWwAAAABJRU5ErkJggg==.png)，其中![](http://od6jpuxvb.bkt.clouddn.com/YsQNYxOAq4SkEuAwnof0KrAZnzZoFZKioqABbqIsXL6amA2EAj+EAEJBIFUFDdeoAAAAASUVORK5CYII=.png)是保留后的候选关键词。

3、构建候选关键词图G = (V,E)，其中V为节点集，由（2）生成的候选关键词组成，然后采用共现关系（co-occurrence）构造任两点之间的边，两个节点之间存在边仅当它们对应的词汇在长度为K的窗口中共现，K表示窗口大小，即最多共现K个单词。

4、根据上面公式，迭代传播各节点的权重，直至收敛。

5、对节点权重进行倒序排序，从而得到最重要的T个单词，作为候选关键词。

6、由5得到最重要的T个单词，在原始文本中进行标记，若形成相邻词组，则组合成多词关键词。

### 使用TextRank提取关键短语

参照“使用TextRank提取关键词”提取出若干关键词。若原文本中存在若干个关键词相邻的情况，那么这些关键词可以构成一个关键短语。

例如，在一篇介绍“支持向量机”的文章中，可以找到三个关键词 支持、向量、机，通过关键短语提取，可以得到

支持向量机.

## [TextRank的scala实现](https://github.com/STHSF/nlp/tree/develop/scala/nlpsuit/src/main/scala/com/kunyandata/nlpsuit/wordExtraction)

程序步骤：

1、**文章分词：** 对每一篇文章进行分词，分词系统主要由坤雁分词系统、ansj分词，结巴分词等。

2、**分词结果数据清洗：** 主要包括去停用词、去除符号字母数字等。

3、**构建候选关键词图：** 根据设定的词语选择窗口截取文本的分词结果，将每个词语作为候选关键词图的节点，截取的每一段文本中的词语作为相邻的边，以此构建候选关键词图。
4、**关键词提取：** 利用pagerank思想循环迭代候选关键词图，


## 参考文献
 * David Adamo: [TextRank](https://github.com/davidadamojr/TextRank)
 * [Automatic Summarization](https://en.wikipedia.org/wiki/Automatic_summarization)
 * [TextRank](http://web.eecs.umich.edu/~mihalcea/papers/mihalcea.emnlp04.pdf)
 * [PageRank](http://ilpubs.stanford.edu:8090/422/1/1999-66.pdf)






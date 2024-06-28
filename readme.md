
# Projects
## Gridworld
de.schmiereck.smkEasyNN.gridworld.GridworldMain

## TicTacToe
de.schmiereck.smkEasyNN.ticTacToe.TicTacToeMain

## World
de.schmiereck.smkEasyNN.world.WorldMain

Ident:
* values between 0.0 an 1.0
* i(r,g,b) sum is always 1.0
* global value of all cells of a individuum

State:
* values between 0.0 an 1.0
* s(r,g,b) values between 0.0 an 1.0
* i(r,g,b) sum of values is always 1.0

Target:
* State == Ident

Input:
* me: i0, s0
* neighbours: i1, s1, i2, s2, i3, s3,

Output:
* transfer +/- x % of differenz between s0 and s1, s2, s3
* transfer +/- y % of differenz between s0 and i1, i2, i3
  * controlled: if i of neighbour node is (nearly) equal to net ident then node is part of the net

Architecture:
* controlled cells
  * every controlled node has a net instance 
    for itself and the neighboring cells
* meta level of controlled neighbour cells
  * meta-net see and affects the average of controlled cells
* Node: Netz für Zelle mit Nachbarn
* Meta-Node: 

```
|-|-|-| |i| |-|-|-|
|-|-|-|  I  |-|-|-|   m
|-|-|  I  |-|-|-|-|   m
|-|-|-|-|  I  |-|-|   m
|-|-|    I    |-|-|   mm

           i0
          /  \
         i1   i2
        /  \ /  \
       i         i
```

# Links
* Recurrent Neural Network & LSTM with Practical Implementation | by Amir Ali | The Art of Data Scicne | Medium
  * https://medium.com/machine-learning-researcher/recurrent-neural-network-rnn-e6f69db16eba
* A Brief Introduction to Recurrent Neural Networks | by Jonte Dancker | Towards Data Science
  * https://towardsdatascience.com/a-brief-introduction-to-recurrent-neural-networks-638f64a61ff4
* Reinforcement Learning (RL)
  * model-free Ansatz
    * https://www.mikrocontroller.net/topic/412417
  * Q-Learning
    * http://outlace.com/rlpart3.html
    * RNN
* Neuronales Netz · Seite · HOOU
  * https://www.hoou.de/projects/neuronale-netze-kurz-erklart/pages/neuronales-netz
* Rekurrentes neuronales Netz – Wikipedia
  * https://de.wikipedia.org/wiki/Rekurrentes_neuronales_Netz
* Long Short-Term Memory Units (kurz: LSTMs)
  * Aufbau einer LSTM-Zelle
    * https://www.bigdata-insider.de/was-ist-ein-long-short-term-memory-a-774848/
  * Keras LSTM to Java
    * https://alexrachnog.medium.com/keras-lstm-to-java-a3124402d69
* Simple neural network implementation in C
    * https://towardsdatascience.com/simple-neural-network-implementation-in-c-663f51447547
* Trouble Understanding the Backpropagation Algorithm in Neural Network
  * https://stackoverflow.com/questions/27280750/trouble-understanding-the-backpropagation-algorithm-in-neural-network
* Implementing a Neural Network in Java: Training and Backpropagation issues\
  (with the template for an MLP in Answer 6) 
  * https://stackoverflow.com/questions/9951487/implementing-a-neural-network-in-java-training-and-backpropagation-issues
* Betreff: Die seltsamen Fehlleistungen neuronaler Netze - Heidelberg Laureate Forum - SciLogs - Wissenschaftsblogs
  * https://scilogs.spektrum.de/hlf/die-seltsamen-fehlleistungen-neuronaler-netze/
* Stephen Wolfram sucht nach der Weltformel der Physik - Spektrum der Wissenschaft
  * https://www.spektrum.de/news/stephen-wolfram-sucht-nach-der-weltformel-der-physik/2203229
* Missing Link: Stephen Wolfram über die Rolle der KI in der Forschung (Teil 2) | heise online
  * https://www.heise.de/hintergrund/Missing-Link-Stephen-Wolfram-ueber-die-Rolle-der-KI-in-der-Forschung-Teil-2-9650643.html 


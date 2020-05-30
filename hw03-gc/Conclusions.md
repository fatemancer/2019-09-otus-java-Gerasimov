###

Сравниваются 5 сборщиков мусора:
- parallel
- serial
- CMS
- ZGC
- G1

Тестовый фреймворк запускает в отдельной JVM длинный цикл с постоянным добавлением новых элементов в список
и удалением половины из них. После вылета OOM, она ловится, результаты записываются в виде json-строки 
и анализируются после завершения работы всех сборщиков.

Основные критерии для сравнения - время, уходящее на сборку мусора (среднее и медианное),
а также максимальное количество обработанных данных (максимальное количество итераций в 
цикле перед его вылетом с OOM)

####64 mb

На малых объёмах лучше всего себя показывает G1 или serial сборщик. 
ZGC не справляется вовсе. 
```
VM with GC: parallel. 
 Mean duration of GC: {Major GC=18.591836734693878, Minor GC=7.3061224489795915} ms 
 Median duration of GC: {Major GC=104.23828125, Minor GC=7.041131422731269} ms
 Last processed index 18, 2911 ms total

VM with GC: CMS. 
 Mean duration of GC: {Major GC=29.727272727272727, Minor GC=6.545454545454546} ms 
 Median duration of GC: {Major GC=69.27008056640625, Minor GC=4.489952087402344} ms
 Last processed index 18, 2550 ms total

VM with GC: ZGC. 
 Mean duration of GC: {Major GC=12.0, Minor GC=0.0} ms 
 Median duration of GC: {Major GC=12.0, Minor GC=0.0} ms
 Last processed index 1, 90 ms total

VM with GC: G1. 
 Mean duration of GC: {Major GC=9.125, Minor GC=9.05} ms 
 Median duration of GC: {Major GC=62.87208271026611, Minor GC=1.925372812136387} ms
 Last processed index 26, 5174 ms total

VM with GC: serial. 
 Mean duration of GC: {Major GC=24.19672131147541, Minor GC=3.19672131147541} ms 
 Median duration of GC: {Major GC=70.45880857110023, Minor GC=0.43203361763153225} ms
 Last processed index 26, 4484 ms total
```

####256 mb

Максимальная производительность с точки зрения длительности работы без сбоя - у serial/G1 сборщика, при этом
G1 даёт меньшие средние и медианные значения времени на major сборку.

```
VM with GC: ZGC. 
 Mean duration of GC: {Major GC=152.900826446281, Minor GC=0.0} ms 
 Median duration of GC: {Major GC=222.474101214987, Minor GC=0.0} ms
 Last processed index 56, 27444 ms total

VM with GC: CMS. 
 Mean duration of GC: {Major GC=122.63730569948187, Minor GC=10.720207253886011} ms 
 Median duration of GC: {Major GC=321.4266428211734, Minor GC=6.249148104645333E-4} ms
 Last processed index 83, 35745 ms total

VM with GC: parallel. 
 Mean duration of GC: {Major GC=62.263392857142854, Minor GC=23.21875} ms 
 Median duration of GC: {Major GC=564.2257118551047, Minor GC=62.47864605086518} ms
 Last processed index 83, 37697 ms total

VM with GC: serial. 
 Mean duration of GC: {Major GC=98.16151202749141, Minor GC=8.106529209621993} ms 
 Median duration of GC: {Major GC=303.9805548128537, Minor GC=3.980160206206511E-14} ms
 Last processed index 111, 63014 ms total

VM with GC: G1. 
 Mean duration of GC: {Major GC=17.021052631578947, Minor GC=26.842105263157894} ms 
 Median duration of GC: {Major GC=122.92931167578337, Minor GC=29.828616780233254} ms
 Last processed index 100, 46648 ms total
```

####1024mb

При дальнейшем увеличении хипа G1 ещё больше догоняет serial по длительности работы, 
 при этом сохраняя меньшие средние и медианные значения времени на major сборку. 

```
VM with GC: CMS. 
 Mean duration of GC: {Minor GC=39.42666666666667, Major GC=277.045} ms 
 Median duration of GC: {Minor GC=4.465183685168429E-6, Major GC=1151.8515232335988} ms
 Last processed index 277, 321323 ms total

VM with GC: parallel. 
 Mean duration of GC: {Minor GC=73.71184022824536, Major GC=160.03994293865907} ms 
 Median duration of GC: {Minor GC=22.17494506860039, Major GC=1588.8528397877408} ms
 Last processed index 277, 341785 ms total

VM with GC: serial. 
 Mean duration of GC: {Minor GC=30.7392138063279, Major GC=301.57622243528283} ms 
 Median duration of GC: {Minor GC=3.5220127382073366E-20, Major GC=993.459537479333} ms
 Last processed index 416, 726986 ms total

VM with GC: G1. 
 Mean duration of GC: {Minor GC=106.48301329394387, Major GC=64.46381093057607} ms 
 Median duration of GC: {Minor GC=216.21572670098087, Major GC=518.4799739235847} ms
 Last processed index 415, 674435 ms total

VM with GC: ZGC. 
 Mean duration of GC: {Minor GC=0.0, Major GC=682.5081967213115} ms 
 Median duration of GC: {Minor GC=0.0, Major GC=626.1446980869958} ms
 Last processed index 277, 442117 ms total
```

Исходя из вышенаписанного, можно считать, что G1 сборщик для нашей искусственной задачи является максимально эффективным.
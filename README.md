# Predictive Diagnostic

## Theoretischer Hintergrund

* diskrete Fourier-Transformation (DFT) und schnelle Fourier-Transformation (FFT) als Optimierung
* Voraussetzung: es sind nur Werte an diskreten, äquidistanten (:exclamation:) Zeitpunkten in einem endlichen Intervall bekannt

## Logstash

* erfordert Java 8 (9 nicht unterstüzt)

## Beats

* [Creating a New Beat](https://www.elastic.co/guide/en/beats/devguide/current/new-beat.html)
* written in [Golang](https://golang.org/)

Anforderung

## Normality Descriptor

* DFT = Diskrete Fouriertransformation, FFT = Schnelle Fouriertransformation
* Metrik: "_for example, the sum of how many log entries have a certain level, per second, for all nodes_"
* Mehrere Metriken ermitteln, zum Bsp. für jedes Log-Leve. Diese sind unabhängig voneinander.
* Input: Anzahl Records/Jahr bei 1 Mill. Records/s: **31,54 Billionen/Jahr** (31.536.000.000.000)
* Output:
  * Anzahl _Errors_ pro Sekunde
  * für
* Segmentierung der Datenmenge:
  * Output für größere Zeitabschnitte zusammenfasssen, zum Beispiel _Errors_ für drei Stunden: 8 Werte/Tag
  * Output wieder einer DFT unterziehen: 

## Anomalies Recorder

* IDFT = Inverse Diskrete Fouriertransformation, IFFT = schnelle Inverse Diskrete Fouriertransformation
* 

| Name                | Last update | Link                                                                                     |
| :------------------ | :---------- | :--------------------------------------------------------------------------------------- |
| JTransforms         | Sep. 15     | [JTransforms](https://sites.google.com/site/piotrwendykier/software/jtransforms)         |
| Apache Commons Math | Aug 16      | [The Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/) |

## Offene Punkte

* systemweit synchronisierte Zeitstempel für alle Log-Quellen

## Mehr

* [Etwas Signal- und Systemtheorie "for Dummies"](http://www.iks.hs-merseburg.de/~tlange/pdf/Etwas%20Signal-%20und%20Systemtheorie.pdf)
* [The Complete Guide to Log and Event Management](https://www.microfocus.com/media/white-paper/the_complete_guide_to_log_and_event_management_wp.pdf)
* [Optimierungsverfahren bei saisonalen/periodischen Prozessen für Prognosezwecke](https://www.inf.tu-dresden.de/content/institutes/iai/tis-neu/lehre/archiv/folien.ss_2010/Vortrag_Harlan.pdf)

## Terms

| Term | Description                               |
| ---- | ----------------------------------------- |
| SIEM | Security Information and Event Management |

## Setup project

```bash
mkdir predictive-diagnostic && cd predictive-diagnostic
git init
gradle init --type java-library
```

## Generate test log

```bash
flog --format apache_error --number 1000000 > ./test-1-Mio.log
docker run -it --rm mingrammer/flog --format apache_error --number 1000000 > ./test-1-Mio.log
```

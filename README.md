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

* Anzahl Records im Jahr bei 1 Mill. Records/s: **31,54 Billionen/Jahr** (31.536.000.000.000)
* => in Lernphase um Faktor 100 schneller

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
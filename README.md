# Purpose

This project was created by the SoftwarE Analytics Research Team (SEART) at Università della Svizzera italiana and The Sofware Design Lab (TSDL) at the Universidad de los Andes. The major goal of the CONAN project is to enable automatic detection of **CON**nectivity issues in **AN**droid apps. Unlike current approaches, CONAN is based on static analysis mechanisms, thus enabling practitioners to identify potential connectivity issues in the early development stages, since all it requires is the source code of the application to analyze.

# Video

<p align="center">
<iframe width="500" height="282" src="https://www.youtube.com/embed/LBBvXdjftVU" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe></p>

# Publications

- _"Studying eventual connectivity issues in Android apps."_, Escobar-Velásquez, Camilo and Mazuera-Rozo, Alejandro and Bedoya, Claudia and Osorio-Riaño, Michael and Linares-Vásquez, Mario and Bavota, Gabriele, _In Empirical Software Engineering 2022_, Journal Article [[DOI](https://doi.org/10.1007/s10664-021-10020-6)] [[Online Appendix](https://thesoftwaredesignlab.github.io/android-eventual-connectivity/)] 
- _"Detecting Connectivity Issues in Android Apps."_, Mazuera-Rozo, Alejandro and Escobar-Velásquez, Camilo and Espitia-Acero, Juan and Linares-Vásquez, Mario and Bavota, Gabriele, _In Proceedings of the 29th IEEE International Conference on Software Analysis, Evolution and Reengineering 2022 (SANER’22)_, Research Track Track [[DOI](https://doi.org/10.1109/SANER53432.2022.00087)]

# CONAN Architecture Overview

Fig. 1 depicts CONAN integration in Android Lint. Overall, Lint's open-closed model allows for the addition of custom checks. Given the set of Java classes defining Android lint internals (___i.e.,___ `com.android.tools.lint`), the main idea behind building customized checks is fundamentally to define custom issues (2) (`Issue::class`) (__i.e.,__ potential bug in an Android application), and declare them in a custom registry(1) (`IssueRegistry::class`) (__i.e.,__ registry which provides a list of checks to be performed on an Android project). Each issue needs to be _**mapped**_ to a custom detector(4) (`Detector::class`) responsible for analyzing the issue as well as its (5) (`Scope::class`), meaning the set of type of files (_e.g.,_ Java files) a detector must consider when performing its analysis. Such a _mapping_ relation is performed by defining a custom Implementation(3) (`Implementation::class`).

![Architecture](./assets/imgs/conan_archi1024_1.png)

## 



    
---
Hosted on GitHub Pages - Theme by [orderedlist](https://github.com/orderedlist)

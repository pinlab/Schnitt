#!/bin/bash

# Program can be run after 'mvn install' was successful

java -cp ../target/lib/*:../target/WavPanel-0.1.jar info.pinlab.snd.gui.swing.WavPanelImpl

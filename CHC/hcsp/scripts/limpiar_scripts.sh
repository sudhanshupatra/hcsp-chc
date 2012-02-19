#!/bin/bash

cd ../ejecuciones

if [ $? != 0 ]; then
	exit $?
fi

mkdir -p scripts_calibracion
mkdir -p scripts_frente_pareto
mkdir -p scripts_evaluacion

rm scripts_calibracion/*
rm scripts_frente_pareto/*
rm scripts_evaluacion/*

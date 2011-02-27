#!/bin/bash

cd ../ejecuciones

if [ $? != 0 ]; then
	exit $?
fi

mkdir -p calibracion/seq
mkdir -p calibracion/lan4
mkdir -p calibracion/lan8
mkdir -p frente_pareto/seq
mkdir -p frente_pareto/lan4
mkdir -p frente_pareto/islas
mkdir -p evaluacion/seq/Braun_et_al
mkdir -p evaluacion/seq/1024x32
mkdir -p evaluacion/seq/2048x64
mkdir -p evaluacion/seq/4096x128
mkdir -p evaluacion/lan4/Braun_et_al
mkdir -p evaluacion/lan4/1024x32
mkdir -p evaluacion/lan4/2048x64
mkdir -p evaluacion/lan4/4096x128
mkdir -p evaluacion/lan8/Braun_et_al
mkdir -p evaluacion/lan8/1024x32
mkdir -p evaluacion/lan8/2048x64
mkdir -p evaluacion/lan8/4096x128
mkdir -p seq
mkdir -p lan4
mkdir -p lan8

rm calibracion/seq/*
rm calibracion/lan4/*
rm calibracion/lan8/*
rm evaluacion/seq/Braun_et_al/*
rm evaluacion/seq/1024x32/*
rm evaluacion/seq/2048x64/*
rm evaluacion/seq/4096x128/*
rm evaluacion/lan4/Braun_et_al/*
rm evaluacion/lan4/1024x32/*
rm evaluacion/lan4/2048x64/*
rm evaluacion/lan4/4096x128/*
rm evaluacion/lan8/Braun_et_al/*
rm evaluacion/lan8/1024x32/*
rm evaluacion/lan8/2048x64/*
rm evaluacion/lan8/4096x128/*
rm frente_pareto/seq/*
rm frente_pareto/lan4/*
rm frente_pareto/islas
rm seq/*
rm lan4/*
rm lan8/*

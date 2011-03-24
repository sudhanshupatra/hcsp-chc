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
mkdir -p frente_pareto/barca/1
mkdir -p frente_pareto/barca/2
mkdir -p frente_pareto/barca/3
mkdir -p frente_pareto/barca/4
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
mkdir -p evaluacion/lan16/Braun_et_al
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
rm evaluacion/lan16/Braun_et_al/*
rm frente_pareto/seq/*
rm frente_pareto/lan4/*
rm frente_pareto/islas/*
rm frente_pareto/barca/1/*
rm frente_pareto/barca/2/*
rm frente_pareto/barca/3/*
rm frente_pareto/barca/4/*
rm seq/*
rm lan4/*
rm lan8/*

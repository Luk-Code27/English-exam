#!/bin/sh
# Packt Datapack und Resourcepack als ZIP nach dist/
set -e
cd "$(dirname "$0")/.."
mkdir -p dist
rm -f dist/*.zip
(cd datapack/exam_slides && zip -qr ../../dist/exam_slides_datapack.zip .)
(cd resourcepack/exam_slides && zip -qr ../../dist/exam_slides_resourcepack.zip .)
ls -l dist

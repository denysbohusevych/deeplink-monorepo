#!/bin/bash
# Запускает генерацию Go-кода внутри Docker контейнера с установленным buf
# Это позволяет не засорять твой компьютер лишними утилитами.

docker run --rm --volume "$(pwd):/workspace" --workdir /workspace bufbuild/buf generate proto --template proto/buf.gen.go.yaml
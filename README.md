# SitePopBlocker

Um navegador Android leve para sites autorizados, com uma única interface adaptada a smartphones, Android TV e Android Box. Bloqueia pedidos de abertura de nova janela e alguns domínios publicitários/telemetria comuns, mantendo a navegação no WebView principal.

## Compatibilidade

- Android 6.0 ou superior
- smartphones, tablets, Android TV e Android Box
- controlo remoto: use as setas para focar a barra de endereço e o botão **Abrir**; o botão Voltar regressa na navegação

## Compilar

Abra esta pasta no Android Studio e execute `app`, ou use:

```sh
gradle assembleDebug
```

O APK de depuração será criado em `app/build/outputs/apk/debug/app-debug.apk`.

## Limites

O bloqueador destina-se a reduzir pop-ups e redirecionamentos em sites que tem autorização para usar. Não remove paywalls, DRM, autenticação ou outras restrições de acesso.

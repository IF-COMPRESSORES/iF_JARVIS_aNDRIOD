IF JARVIS — VOSK ANDROID 0.3

Sem Picovoice.
Sem AccessKey.
Sem Secret obrigatório.

O GitHub Actions:
1. baixa o modelo Vosk PT-BR;
2. compila o aplicativo;
3. gera o APK para download.

OBJETIVO DESTA VERSÃO
- Android inicia um ForegroundService de microfone com o app visível.
- Vosk continua ouvindo localmente.
- Ao detectar "Jarvis" (ou variações próximas), envia o evento para a interface.
- A interface atual do Jarvis é ativada.
- Uma notificação "IF Jarvis ativo" permanece enquanto o detector estiver rodando.

LIMITAÇÃO DESTA ETAPA
Esta versão é o teste do detector nativo + segundo plano.
O Android pode limitar a abertura automática da tela a partir do background.
A etapa seguinte é integrar VoiceInteractionService para o Jarvis ser tratado pelo Android como assistente.

GITHUB
Suba TODO o conteúdo desta pasta para a raiz do repositório.
Depois: Actions > Build IF Jarvis Vosk APK > Run workflow.

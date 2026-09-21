IF JARVIS — ANDROID 0.5

Arquitetura atual:
- App Android com WebView local.
- Interface principal em app/src/main/assets/jarvis.html.
- Integração com Supabase para autenticação e dados da IF.
- Reconhecimento de voz pelo Android via RecognizerIntent.
- Resposta por voz via Android TextToSpeech.
- Teclado como fallback.
- Build pelo GitHub Actions.

IMPORTANTE
- Esta versão não usa Vosk.
- Esta versão não mantém microfone ouvindo continuamente.
- O botão de microfone continua como fallback/laboratório, não como objetivo final do produto.

OBJETIVO DO PROJETO
O Jarvis será o assistente inteligente integrado ao App IF, com comportamento semelhante ao conceito de “OK Google” / “Hey Siri”. O alvo é permitir ativação do assistente pelo Android, conversa por voz, acesso contextual aos dados do sistema IF e, posteriormente, wake word local “Jarvis”.

PRÓXIMA ETAPA — 0.6
1. Registrar o IF Jarvis como VoiceInteractionService.
2. Criar VoiceInteractionSessionService e VoiceInteractionSession.
3. Permitir que o usuário selecione o IF Jarvis como assistente padrão do Android.

Depois dessa fundação serão adicionados áudio contínuo de sessão, wake word e camada de IA.

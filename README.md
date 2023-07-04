# bidrag-inntekt

![](https://github.com/navikt/bidrag-inntekt/workflows/continuous%20integration/badge.svg)
![](https://github.com/navikt/bidrag-inntekt/workflows/release%20bidrag-inntekt/badge.svg)

Repository som inneholder funksjonalitet relatert til inntekter

## Utstede gyldig token i dev-gcp
For å kunne teste applikasjonen i `dev-gcp` trenger man et gyldig AzureAD JWT-token. 
JWT-tokenet kan hentes ut manuelt eller ved hjelp at skriptet her: [hentJwtToken](https://github.com/navikt/bidrag-dev/blob/main/scripts/hentJwtToken.sh).

For å utstede et slikt token trenger man miljøvariablene `AZURE_APP_CLIENT_ID` og `AZURE_APP_CLIENT_SECRET`. Disse ligger tilgjengelig i de kjørende pod'ene til applikasjonen.

Koble seg til en kjørende pod (feature-branch):
```
kubectl -n bidrag exec -i -t bidrag-inntekt-feature-<sha> -c bidrag-inntekt-feature -- /bin/bash
```

Koble seg til en kjørende pod (main-branch):
```
kubectl -n bidrag exec -i -t bidrag-inntekt-<sha> -c bidrag-inntekt -- /bin/bash
```

Når man er inne i pod'en kan man hente ut miljøvariablene på følgende måte:
```
echo "$( cat /var/run/secrets/nais.io/azure/AZURE_APP_CLIENT_ID )"
echo "$( cat /var/run/secrets/nais.io/azure/AZURE_APP_CLIENT_SECRET )"
```

Deretter kan vi hente ned et gyldig Azure AD JWT-token med følgende kall (feature-branch): 
```
curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'client_id=<AZURE_APP_CLIENT_ID>&scope=api://dev-gcp.bidrag.bidrag-inntekt-feature/.default&client_secret=<AZURE_APP_CLIENT_SECRET>&grant_type=client_credentials' 'https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token'
```

Deretter kan vi hente ned et gyldig Azure AD JWT-token med følgende kall (main-branch):
```
curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'client_id=<AZURE_APP_CLIENT_ID>&scope=api://dev-gcp.bidrag.bidrag-inntekt/.default&client_secret=<AZURE_APP_CLIENT_SECRET>&grant_type=client_credentials' 'https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token'
```

## Kjøre applikasjon lokalt
Applikasjonen kan kjøres opp for å teste endepunkter fra Swagger ([http://localhost:8080/bidrag-inntekt](http://localhost:8080/bidrag-inntekt)).

For å starte applikasjonen kjører man `main`-metoden i fila `BidragInntektLocal.kt` med profilen `local`.

Kjør følgende kommando for å hente nødvendige miljøvariabler for å starte opp applikasjonen. Pass på å ikke commite filen til GIT.
```bash

kubectl exec --tty deployment/bidrag-inntekt printenv | grep -E 'AZURE_|_URL|SCOPE' > src/test/resources/application-lokal-nais-secrets.properties
```
Også når man kjører applikasjonen lokalt vil man trenge et gyldig JWT-token for å kunne kalle på endepunktene. For å utstede et slikt token kan man benytte det åpne endepunktet `GET /local/cookie/` med `issuerId=aad` og `audience=aud-localhost`. Her benyttes en "fake" token-issuer som er satt med wiremock ved hjelp av annotasjonen: `@EnableMockOAuth2Server` fra NAV-biblioteket `token-support`.


## Testing i Swagger
Applikasjonen testes enklest i Swagger (for generering av gyldig token, se over):
```
https://bidrag-inntekt.intern.dev.nav.no/bidrag-inntekt/swagger-ui/index.html?configUrl=/bidrag-inntekt/v3/api-docs/swagger-config#/inntekt-controller
```

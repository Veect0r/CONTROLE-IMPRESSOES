# 📄 Controle de Impressões

## Sobre o projeto

Este projeto nasceu da necessidade de otimizar o controle de impressões utilizado no ambiente de trabalho.

Inicialmente, o processo era parcialmente automatizado por meio de uma planilha Excel e de um pequeno programa desenvolvido em C. O programa permitia registrar as informações, porém os dados ainda precisavam ser transferidos manualmente para a planilha, tornando o processo repetitivo e sujeito a erros. Além disso, a soma das impressões era realizada manualmente pela equipe.

Com base nessa necessidade, utilizei o código existente como ponto de partida e desenvolvi uma nova solução utilizando **Java** e **Spring Boot**, automatizando todo o fluxo de registro.

A nova aplicação:

* Elimina a necessidade de preenchimento manual da planilha;
* Integra-se diretamente ao Google Sheets por meio da API oficial;
* Registra automaticamente cada solicitação de impressão;
* Reduz erros operacionais;
* Agiliza o trabalho da equipe, tornando o processo totalmente automatizado.

O foco do desenvolvimento foi a lógica de negócio, a integração com a API do Google Sheets e a arquitetura da aplicação. A interface web foi construída com apoio de ferramentas de IA para acelerar o desenvolvimento do frontend, enquanto toda a implementação da aplicação, integração, regras de negócio e automação foram desenvolvidas por mim.

## Tecnologias

* Java 21
* Spring Boot
* Maven
* Thymeleaf
* Google Sheets API
* HTML
* CSS

## Objetivo

Demonstrar a aplicação prática de Java e Spring Boot na resolução de um problema real, automatizando um processo interno e eliminando tarefas repetitivas por meio da integração com serviços externos.


## Configuração

A aplicação não mantém o ID da planilha nem as credenciais do Google no código versionado.

Antes de iniciar:

1. Coloque `service-account.json` em `src/main/resources/credentials/`.
2. Defina a variável de ambiente `GOOGLE_SHEETS_SPREADSHEET_ID` com o ID da planilha.
3. Se `application.properties` não existir localmente, copie `src/main/resources/application.properties.example` para `src/main/resources/application.properties`.

No Windows PowerShell:

```powershell
$env:GOOGLE_SHEETS_SPREADSHEET_ID="SEU_ID_DA_PLANILHA"
```

Para deixar a variável persistente para seu usuário:

```powershell
[Environment]::SetEnvironmentVariable("GOOGLE_SHEETS_SPREADSHEET_ID", "SEU_ID_DA_PLANILHA", "User")
```

### Se o `application.properties` já foi commitado

Adicionar o arquivo ao `.gitignore` **não remove um arquivo que já está sendo rastreado pelo Git**. Execute:

```bash
git rm --cached src/main/resources/application.properties
git add .gitignore src/main/resources/application.properties.example
git commit -m "chore: remove configuracao local do controle de versao"
git push
```

Isso impede novos commits de enviarem o arquivo. O ID antigo, porém, continua no histórico dos commits anteriores. Se o repositório for público e você quiser remover esse histórico, será necessário reescrever o histórico do Git e fazer force push coordenado com quem tiver clonado o repositório.

### Concorrência

O registro de impressões usa um lock por instância da aplicação para impedir que duas requisições simultâneas do mesmo servidor façam `ler -> calcular -> escrever` ao mesmo tempo. Isso evita a perda de atualizações no cenário normal em que existe uma única instância do sistema.

Para múltiplas instâncias do sistema, a solução recomendada é armazenar cada evento de impressão em uma operação de `append` e calcular os totais a partir dos eventos, ou migrar essa parte para um banco de dados com transação.

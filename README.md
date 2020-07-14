# MAPS - Financial Market Simulation

O presente teste foi desenvolvido tendo em vista o **nível 3**. 

No repositório do GitHub foram criadas 3 branches, uma para os requisitos de cada nível, e a versão final do teste está na branch master. 

Não foi implementado uma interface web no teste, pois além de ser opcional para o nível 3, optou-se por priorizar os requisitos de back-end em Java. Assim, todos os testes e verificações foram realizados via testes de unidade e de integração, bem como por testes da API REST por meio do aplicativo Postman.

**Execução standalone:** via Maven: na pasta raiz do projeto: 
- Compilação da aplicação: mvn compile
- Execução dos testes de integração: mvn integration-test
- Execução da aplicação: mvn spring-boot:run

**Link plataforma cloud:** https://financialmaps.herokuapp.com/

O deployment na Heroku foi configurado para ser automático a partir de qualquer commit na branch master do presente repositório do GitHub. 

**Opção escolhida no nível 3: A - Multi-usuário.**	Para o cumprimento dos requisitos descritos no teste foi adicionada uma entendidade User e vinculada uma conta corrente a cada usuário (OneToOne). Cada usuário possui username e password previamente gravados e através de um simples mecanismo de autenticação implementado pode acessar o sistema e ter acesso somente a sua conta corrente. Cada requisição deverá ser enviada contendo as credenciais através de cabeçalho HTTP Basic, por ex.: Authorization: Basic am9hbzoxMjM0NTY=. A cada requisição que chame um serviço que manipule a entidade 'Conta Corrente' é buscado o usuário corrente da aplicação e por consequente a conta corrente vinculada a este usuário é que será manipulada.

**Thread-Safe e Concorrência:** Para garantir que não haverá conflitos entre threads concorrentes, os métodos que manipulam ativos e conta corrente e os podem alterar de alguma maneira (inclusão de movimentações, lançamentos e preços de mercado) foram alterados para garantir sua exclusão mútua, ou seja, um procedimento que deseja utilizar algum desses métodos deverá esperar o término de outro procedimento que já esteja utilizando os métodos desejados. No caso da linguagem Java, adicionando a palavra reservada 'synchronized' nos métodos desejados teremos o comportamento esperado. 
	
**Testes unitários e de integração:** Buscou-se cobrir o máximo de situações possíveis na aplicação, mas eventualmente podem ocorrer situações não mapeadas. Foram testadas as regras de negócio implementadas com as possíveis exceptions e cálculos esperados, os endpoints REST com os status retornados e a autenticação/autorização implementada. Foi utilizado o MockMvc para o teste de integração e Mockito para testes unitários. Buscou-se pelo menos uma cobertura de testes acima de 85% do código, sem descontar anotações e classes de configurações.

**Dados pré cadastrados:** Ao iniciar a aplicação foram inseridos: 
- Ativos com nomes de "ATIVO0" até "ATIVO127";
- Valores de mercado com valor 10.00 para todos os ativos acima para o dia 2020-01-02;
- Usuários com username de "usuario0" até "usuario9", e password de "senha0" até "senha9" com saldo inicial igual a 0.00 na conta corrente;
- Usuário administrativo com username "root" e password "spiderman" com saldo inicial igual a 100.00 na conta corrente;
- Usuário criado para o teste de integração com username "usuario-teste" e password "integracao" com saldo inicial igual a 1000.00 na conta corrente;

**Frameworks e Tecnologias Utilizadas:** Para o desenvolvimento deste teste foram utilizados:
- Java 8
- Spring Boot / Spring Data / Spring Security
- Banco de dados H2
- JPA / Hibernate 5
- JUnit
- Project Lombok

A escolha pelo banco de dados H2 se deve por este ser um banco executado em memória e de fácil configuração para execução standalone e em nuvem. 
O Projeto Lombok possui annotations para minimizar o uso de código repetitivo e melhorar a produtividade. Mais informações em: [https://projectlombok.org/](https://projectlombok.org/)


**HTTP Status: ** Os HTTP status codes utilizados neste teste foram:
- **200 (OK):** Para requisições bem sucedidas;
- **201 (CREATED):** Para criação com sucesso de novos recursos;
- **401 (UNAUTHORIZED):** Para qualquer requisição sem autenticação prévia ou HTTP basic no HEADER;
- **403 (FORBIDDEN):** Quando usuário logado não possuir permissão requerida para a ação desejada;
- **404 (NOT FOUND):** Quando um objeto buscado não é encontrado;
- **406 (NOT ACCEPTABLE):** Quando ação desejada não cumpre os requisitos de implementação; 
- **500 (INTERNAL SERVER ERROR):** Para exceptions não mapeadas na aplicação;

**Exemplos de HTTP Status 403:** Usuário administrador tenta cadastrar lançamentos ou movimentações ou usuário comum tenta criar, alterar ou deletar um ativo.

**Exemplos de HTTP Status 406:** Conta corrente sem saldo suficiente, quantidade de ativo insuficiente para venda, movimentação em data não permitida ou cadastro de ativo com data de emissão maior que data de vencimento. 


### Autor:
Elisson Francisco da Silva

email: elissonfs@gmail.com

cel: (12)997477873

linkedIn: elissonfs

**Observação sobre implementação de interface web:** Possuo dois repositórios públicos no GitHub com a implementação full-stack de uma simulação de sistema web. Fiquem a vontade de avaliá-los também caso se interessem.
- Back-end: Java e SpringBoot: [https://github.com/elissonfs13/futebol-simulador-api](https://github.com/elissonfs13/futebol-simulador-api)
- Front-end: TypeScript e Angular 9: [https://github.com/elissonfs13/futebol-simulador-ui](https://github.com/elissonfs13/futebol-simulador-ui) 
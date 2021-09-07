# HTTP 서버 구현하기

## 1단계 - HTTP 서버 구현하기

1. GET /index.html 응답하기
2. Query String 파싱
3. HTTP Status Code 302
4. POST 방식으로 회원가입
5. CSS 지원하기

## 구현해야할 기능

- GET 요청 시 Query-String Request 받기
- 각 요청 별 테스트 코드 작성하기

## ISSUE

- ControllerAdvice 의 에러처리는 그 Map에 넣어둔 순서에 따라 영향을 받음.(Exception을 가장 먼저 넣게 되면 모든 Exception의 처리는 그 Map에
  정의된 방법으로 진행)
  -> 현재 기능 구현을 LinkedHashMap으로 진행했지만, 실제로 ExceptionHandler에서는 어떻게 처리하는지 알아보고 이를 적용해보기(깊이가 가장 가까운
  ExceptionHandling이 되는 것으로 알고 있음.)
- ViewResolver에서의 에러 처리를 진행한다고 하자. 이게 서버 내부적인 로직의 문제이기 때문에, 500 response를 보내줘야 하나?(나의 View는 사용자에게 직접
  노출되지 않는다.)
- "text/html" 과 같은 파일 형식을 직접 file로 부터 얻어왔었는데, 이는 기존의 테스트가 깨지는 현상 ("text/html;charset=utf-8" 필요)발생.
  ContentType이라는 클래스를 별도로 분리하여 관리할지 고민
- ErrorPage로의 Redirection로직을 HttpStatus.Found를 통해 우선적으로 Browser가 Redirect요청을 보내도록 구성했다. js로 이를 별도로
  처리하거나, Response 자체를 HttpStatus.ok 에 파일을 보내줄 수도 있을듯 하다.
- HttpRequest에서 HttpSessions에 쿠기에 있는 Session이 존재하지 않는 경우 새로 발급. 새로 발급 받은 토큰과 관계없이 httpSession의 값으로
  setCookie 진행. containSession()의 값이 true인 경우에도 setCookie를 진행해줘야할 때가 있음. SessionHandler의 if 문으로 분기
  처리로 우선 진행
- [x] HttpSessions에 등록되지 않은 JSESSIONID로 요청이 들어 왔을 때, 새로운 JSESSIONID를 발급해줘야하는지, 아니면 요청에서 들어온 JSESSIONID로
  HttpSessions에 등록을 해야하는지 -> domain별로 Cookie가 관리되어 외부에서 마든 JSESSIONID가 요청되는 경우는 매우 드물고, 오히려 만료된 Session인 경우를 핸들링하는 경우가 많은 듯. 기존의 JSESSIONID를 없애고 새로운 JSESSIONID Set-Cookie하도록 구현.
- HttpResponse로 n개의 Set-Cookie가 가능해야함. -> HttpHeaders가 Map<String,String> 구조가 아닌 Map<String, List<String>> (또는 MultiValueMap<String, String>)의 구조를 가져야 할듯 -> [Springframework.http.Httpheaders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html) -> 이번 구현에서는 기존의 것을 유지하고, 한개의 Set-Cookie만을 허용.


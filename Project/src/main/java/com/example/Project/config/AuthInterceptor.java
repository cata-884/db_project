package com.example.Project.config;

import com.example.Project.service.SesiuneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.Set;

/**
 * Interceptor de autentificare ce protejeaza toate rutele {@code /api/**}.
 *
 * <p>Inainte de fiecare cerere HTTP, verifica prezenta si validitatea token-ului Bearer
 * din header-ul {@code Authorization}. Daca token-ul lipseste sau este expirat, returneaza
 * imediat HTTP 401 fara a mai apela controller-ul.</p>
 *
 * <p>Rutele din {@code RUTE_PUBLICE} (login, register) sunt exceptate de la verificare.</p>
 *
 * @see WebConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SesiuneService sesiuneService;

    /**
     * Whitelist. Rute ce pot fi accesate fara token de autentificare.
     */
    private static final Set<String> RUTE_PUBLICE = Set.of(
        "/api/auth/login",
        "/api/auth/register"
    );

    /**
     * Verifica autentificarea inainte ca cererea sa ajunga la controller.
     *
     * <p>Fluxul de validare:
     * <ol>
     *   <li>Cererile {@code OPTIONS} (CORS preflight) sunt lasate sa treaca neconditionat.</li>
     *   <li>Rutele publice si non-API sunt lasate sa treaca neconditionat.</li>
     *   <li>Se extrage token-ul Bearer din header-ul {@code Authorization}.</li>
     *   <li>Token-ul este validat in tabelul {@code sesiuni}; daca este valid, {@code idClientCurent}
     *       si {@code tokenCurent} sunt setate ca atribute pe request.</li>
     * </ol>
     * </p>
     *
     * @param req Cererea HTTP curenta.
     * @param res Raspunsul HTTP; poate fi scris direct in caz de eroare de autentificare.
     * @param handler Handler-ul tinta (controller).
     * @return {@code true} daca cererea poate continua, {@code false} daca a fost respinsa cu 401.
     * @throws Exception Daca apare o eroare in timpul scrierii raspunsului de eroare.
     */
    @Override
    public boolean preHandle(HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull Object handler) throws Exception {
        /*
          Browserele trimit o cerere de tip "OPTIONS" înainte de cea reală (pentru securitate CORS).
          Codul le lasă să treacă mereu.

          Options trimite configuratia web
         */
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }

        String path = req.getRequestURI();

        if (RUTE_PUBLICE.stream().anyMatch(path::startsWith)) {
            return true;
        }

        if (!path.startsWith("/api/")) {
            return true;
        }
        /*
          Trimitem mesaj de eroare daca nu putem extrage un token bearer sau daca linia din header e goala.
         */
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Token lipsa\"}");
            return false;
        }

        /*
            Ne asiguram ca tokenul bearer e legat de un client
         */
        String token = authHeader.substring("Bearer ".length()).trim();
        Optional<Long> idClient = sesiuneService.validareToken(token);
        if (idClient.isEmpty()) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Token invalid sau expirat\"}");
            return false;
        }
        /*
            Setam {id, token} din obiectul HttpServletRequest,
             pentru a putea fi preluate ulterior in controller sau alte componente
         */
        req.setAttribute("idClientCurent", idClient.get());
        req.setAttribute("tokenCurent", token);
        return true;
    }
}

package servlets;
import DTO.DTOCandidate;
import battlefield.Battlefield;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import user_types.ally.Ally;
import user_types.ally.AllyConstant;
import user_types.uboat.UBoat;
import user_types.uboat.UboatConstant;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "servlets.GetAllCandidateServlet", urlPatterns = "/get_all_candidate")
public class GetAllCandidateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Ally ally = (Ally) req.getSession().getAttribute(AllyConstant.ALLY_OBJECT);
        UBoat uBoat = (UBoat) req.getSession().getAttribute(UboatConstant.UBOAT_OBJECT);
        Battlefield battlefield = ServletUtils.getBattlefieldFromSession(req.getSession(), getServletContext());

        Gson gson = new Gson();
        if (ally != null) {
            String candidatesListJson = gson.toJson(ally.getCandidateListDelta());
            resp.getWriter().print(candidatesListJson);
        } else if (uBoat != null) {
            List<DTOCandidate> candidateList = uBoat.getCandidateListDelta();
            candidateList.stream()
                    .filter(battlefield::checkIfWin)
                    .findFirst()
                    .ifPresent(battlefield::setWinner);

            String candidatesListJson = gson.toJson(candidateList);
            resp.getWriter().print(candidatesListJson);
        }
    }
}

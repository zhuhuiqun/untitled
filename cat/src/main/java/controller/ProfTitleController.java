package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import domain.ProfTitle;
import service.ProfTitleService;
import util.JSONUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * 将所有方法组织在一个Controller(Servlet)中
 */
@WebServlet("/proftitle.ctl")
public class ProfTitleController extends HttpServlet {
    //请使用以下JSON测试增加功能（id为空）
    //{"description":"id为null的新头衔","no":"05","remarks":""}
    //请使用以下JSON测试修改功能
    //{"description":"修改id=1的头衔","id":1,"no":"05","remarks":""}

    /**
     * POST, http://49.234.218.81:8080/ProfTitle.ctl, 增加头衔
     * 增加一个头衔对象：将来自前端请求的JSON对象，增加到数据库表中
     * @param request 请求对象
     * @param response 响应对象
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        //根据request对象，获得代表参数的JSON字串
        String ProfTitle_json = JSONUtil.getJSON(request);

        //将JSON字串解析为ProfTitle对象
        ProfTitle ProfTitleToAdd = JSON.parseObject(ProfTitle_json, ProfTitle.class);
        //前台没有为id赋值，此处模拟自动生成id。如果Dao能真正完成数据库操作，删除下一行。
        ProfTitleToAdd.setId(4 + (int)(Math.random()*100));

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加ProfTitle对象
        try {
            ProfTitleService.getInstance().add(ProfTitleToAdd);
            message.put("message", "增加成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * DELETE, http://49.234.218.81:8080/ProfTitle.ctl?id=1, 删除id=1的头衔
     * 删除一个头衔对象：根据来自前端请求的id，删除数据库表中id的对应记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //读取参数id
        String id_str = request.getParameter("id");
        int id = Integer.parseInt(id_str);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();

        //到数据库表中删除对应的头衔
        try {
            ProfTitleService.getInstance().delete(id);
            message.put("message", "删除成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }


    /**
     * PUT, http://49.234.218.81:8080/ProfTitle.ctl, 修改头衔
     *
     * 修改一个头衔对象：将来自前端请求的JSON对象，更新数据库表中相同id的记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        String ProfTitle_json = JSONUtil.getJSON(request);
        //将JSON字串解析为ProfTitle对象
        ProfTitle ProfTitleToAdd = JSON.parseObject(ProfTitle_json, ProfTitle.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改ProfTitle对象对应的记录
        try {
            ProfTitleService.getInstance().update(ProfTitleToAdd);
            message.put("message", "修改成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            e.printStackTrace();
        }catch(Exception e){
            message.put("message", "网络异常");
            e.printStackTrace();
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * GET, http://49.234.218.81:8080/ProfTitle.ctl?id=1, 查询id=1的头衔
     * GET, http://49.234.218.81:8080/ProfTitle.ctl, 查询所有的头衔
     * 把一个或所有头衔对象响应到前端
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //读取参数id
        String id_str = request.getParameter("id");

        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id = null, 表示响应所有头衔对象，否则响应id指定的头衔对象
            if (id_str == null) {
                responseProfTitles(response);
            } else {
                int id = Integer.parseInt(id_str);
                responseProfTitle(id, response);
            }
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            e.printStackTrace();
            //响应message到前端
            response.getWriter().println(message);
        }catch(Exception e){
            message.put("message", "网络异常");
            e.printStackTrace();

            //响应message到前端
            response.getWriter().println(message);
        }
    }
    //响应一个头衔对象
    private void responseProfTitle(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找头衔
        ProfTitle ProfTitle = ProfTitleService.getInstance().find(id);
        String ProfTitle_json = JSON.toJSONString(ProfTitle);

        //响应ProfTitle_json到前端
        response.getWriter().println(ProfTitle_json);
    }
    //响应所有头衔对象
    private void responseProfTitles(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有头衔
        Collection<ProfTitle> ProfTitles = ProfTitleService.getInstance().findAll();
        String ProfTitles_json = JSON.toJSONString(ProfTitles, SerializerFeature.DisableCircularReferenceDetect);

        //响应ProfTitles_json到前端
        response.getWriter().println(ProfTitles_json);
    }
}

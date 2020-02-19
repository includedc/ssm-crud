package com.daocun.controller;

import com.daocun.bean.Employee;
import com.daocun.bean.Msg;
import com.daocun.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

//    @RequestMapping("/emps")
//    public String getEmps(@RequestParam(value = "pn",defaultValue = "1") Integer pn, Model model){
//        PageHelper.startPage(pn,5);
//        List<Employee> emps = employeeService.getAll();
//        PageInfo page=new PageInfo(emps,5);
//        model.addAttribute("pageInfo",page);
//        return "list";
//    }
    @ResponseBody
    @RequestMapping(value = "/emp",method = RequestMethod.POST)
    public Msg saveEmp(@Valid Employee employee, BindingResult result){
        if (result.hasErrors()){
            Map<String,Object> map=new MapperMethod.ParamMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                System.out.println("错误的字段名"+error.getField());
                System.out.println("错误信息"+error.getDefaultMessage());
                map.put(error.getField(),error.getDefaultMessage());
            }
            return Msg.fail().add("errorFields",map);
        }else{

            employeeService.saveEmp(employee);
            return Msg.success();
        }

    }
//    检测名字是否可用
    @ResponseBody
    @RequestMapping("/checkuser")
    public Msg checkuser(@RequestParam("empName") String empName){
        String regex="(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
        if(!empName.matches(regex)){
            return Msg.fail().add("va_msg","用户名必须是6-16位数字和字母的组合或者是2-5位汉字");
        }
        boolean b= employeeService.checkUser(empName);
        if (b){
            return Msg.success();
        }else{
            return Msg.fail().add("va_msg","用户名不可用");
        }
    }

    @ResponseBody
    @RequestMapping("/emps")
    public Msg getEmpsWithJson(@RequestParam(value = "pn",defaultValue = "1") Integer pn, Model model){
        PageHelper.startPage(pn,5);
        List<Employee> emps = employeeService.getAll();
        PageInfo page=new PageInfo(emps,5);
        return Msg.success().add("pageInfo",page);
    }

    @ResponseBody
    @RequestMapping(value = "/emp/{id}",method =RequestMethod.GET)
    public Msg getEmp(@PathVariable("id") Integer id){
        Employee employee= employeeService.getEmp(id);
        return Msg.success().add("emp",employee);
    }

    @ResponseBody
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
    public Msg saveEmp(Employee employee){
        employeeService.updateEmp(employee);
        return Msg.success();
    }

//    单个删除和批量删除二合一
    @ResponseBody
    @RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
    public Msg delete(@PathVariable("ids") String ids){
        if (ids.contains("-")){
            List<Integer> del_ids=new ArrayList<>();
            String[] str_ids = ids.split("-");
            for (String str_id : str_ids) {
                del_ids.add(Integer.parseInt(str_id));
            }
            employeeService.deleteBatch(del_ids);
        }else{
            Integer id=Integer.parseInt(ids);
            employeeService.deleteEmp(id);
        }
        return Msg.success();
    }

}

<template>
    <div class="main" style="background-color: #f2f8ee">
      <div class="container">
        <el-row style="width: 100%;">
          <el-col :span="2" >
          </el-col>
  
          <el-col :span="8" >
            <h5 @click="goBack" style="cursor: pointer; color: #549247; text-align: left; margin: 0px 0px 30px;">
              返回登录页面
            </h5>
            
            <!-- 用户注册 -->
            <h1 style="margin-bottom: 30px;">欢迎注册<br>小组项目云管理系统</h1>

            <el-form :model="registerForm"  :rules="rules" ref="registerFormRef">
                <el-form-item label="昵称：&emsp;&emsp;" prop="nickname">
                    <el-input placeholder="请输入昵称" v-model="registerForm.nickname" clearable/>
                </el-form-item>
                <el-form-item label="密码：&emsp;&emsp;" prop="password" style="margin-bottom: 3px;">
                    <el-input placeholder="请输入密码" v-model="registerForm.password"  type="password" show-password/>
                </el-form-item>
                <p style="color:darkgray;font-size:12px ;text-align: left;margin-bottom: 15px;" >
                    长度不少于 8 位，不允许包含空格，至少包含以下四种类型中的三种：大写字母、小写字母、数字、特殊字符。
                </p>
                <el-form-item  label="确认密码："  prop="confirmPassword" >
                    <el-input placeholder="请再次输入密码" v-model="registerForm.confirmPassword"  type="password" show-password/>
                </el-form-item>
                <el-form-item  label="手机号码："  prop="phoneNumber" >
                    <el-input  placeholder="请输入手机号码" v-model="registerForm.phoneNumber" clearable />
                </el-form-item>
                <el-form-item label="验证码：&emsp;"  prop="code" >
                    <el-input placeholder="请输入验证码" v-model="registerForm.code" style="width: 60%; margin-right: 5%;"/>
                    <button type="button" class="btn2" :disabled="countdown > 0" style="width: 35%; margin-left: 0%; height: 100%; font-size: 100%;" @click="sendVerificationCode">
                        {{ countdown > 0 ? `重新发送(${countdown})` : '发送验证码' }}
                    </button>
                </el-form-item>
                <el-form-item style="margin: 30px 0px 50px;">
                <button type="button" class='btn1'  @click="RegisterClick" style="width: 70%;">注册</button>
                </el-form-item>
            </el-form>
          </el-col>

          <el-col :span="14" style="display: flex; justify-content: center; align-items: center;">
            <img src="../assets/image2.png" alt="" style="width:80%;">
          </el-col>

        </el-row>
      </div>
    </div>
  </template>
  
  
  
  <script>
    import {reactive, ref} from 'vue'
    import {ElMessage} from "element-plus";
    import router from "@/router";
    import {register, sendVerificationCodeApi} from "@/api/api";

    export default {
    setup() {

      const registerForm = reactive({
        nickname: "",
        password: "",
        confirmPassword:"",
        phoneNumber: "",
        code: "",
      });

      const registerFormRef = ref(null);

      //两次密码一致性检验
      const DoubleCheckPassword = (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次密码不一致!'))
        } else {
          callback()
        }
      }

      //密码格式校验：长度不少于 8 位，不允许包含空格，至少包含以下四种类型中的三种：大写字母、小写字母、数字、特殊字符
      //其中特殊字符包括：._~!@#$^&*
      const CheckPassword = (rule, value, callback) => {  
        if (value.includes(' ')) {  
          callback(new Error('密码中不允许包含空格'));  
          return;  
        }  
        if (value.length < 8){
          callback(new Error('密码长度需不少于 8 位'));  
          return;  
        }
        const lowerCase = /[a-z]/;  
        const upperCase = /[A-Z]/;  
        const phoneNumber = /[0-9]/;  
        const specialChar = /[._~!@#$^&*]/;  
        
        let count = 0;  
        if (lowerCase.test(value)) count++;  
        if (upperCase.test(value)) count++;  
        if (phoneNumber.test(value)) count++;  
        if (specialChar.test(value)) count++;  
        
        if (count >= 3) {  
          callback();
        } else {  
          callback(new Error('密码应包含英文字母大小写、数字和特殊符号中至少三种'));  
        }
      };
    
      //手机号11位数字检验
      const checkPhoneNumber = (rule, value, callback) => {  
        const phoneRegex = /^1[3-9]\d{9}$/;

        if (phoneRegex.test(value)) {  
          callback(); 
        } else {
          callback(new Error('手机号格式不正确')); 
        }
      }; 

      const rules = {
        nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' },],
        password: [{ required: true, message: '密码不能为空', trigger: 'blur' },
                    { validator: CheckPassword, trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '检验密码不能为空', trigger: 'blur' },
                        { validator: DoubleCheckPassword, trigger: 'blur' }],
        phoneNumber: [{ required: true, message: '电话号码不能为空', trigger: 'blur' },
                { validator: checkPhoneNumber, trigger: 'blur' }],
        code: [{ required: true, message: '验证码不能为空', trigger: 'blur' },]
      };

      const countdown = ref(0);
      // 倒计时功能
      const startCountdown = () => {
        countdown.value = 60;
        const timer = setInterval(() => {
          countdown.value -= 1;
          if (countdown.value <= 0) {
            clearInterval(timer);
          }
        }, 1000);
      };

      // 注册逻辑
      const RegisterClick = async () => {
        try {
          // 验证表单
          await registerFormRef.value.validate();

          console.log(registerForm);
        
          const response = await register({
            nickname: registerForm.nickname,
            password: registerForm.password,
            phoneNumber: registerForm.phoneNumber,
            code: registerForm.code
          });

          if (response.data.success) {
            ElMessage.success("注册成功");
            router.push('/'); // 注册成功后返回登录页面
          } else {
            ElMessage.error(response.data.message || "注册失败");
          }
        } catch (error) {
          ElMessage.error("请检查输入信息");
        }
      };

    // 验证码发送逻辑
      const sendVerificationCode = async () => {
        if (!registerForm.phoneNumber) {
          ElMessage.warning("请先填写手机号");
          return;
        }
        if (countdown.value > 0) {
          return;
        }
        try {
          // 模拟发送验证码请求
          const formData = new FormData();
          formData.append('phoneNumber', registerForm.phoneNumber);
          formData.append('choice', 0);
          const response = await sendVerificationCodeApi(formData);
          if (response.data === "repeated") {
            ElMessage.warning("该手机号已被注册，请更换手机号码后重试");
          }
          else {
            ElMessage.success("验证码已发送");
            startCountdown();
          }
        } catch (error) {
          ElMessage.error("验证码发送失败");
        }
      };
  
      const goBack = () => {
        ElMessage.success("返回登录成功");
        router.push('/')
    };
  
      return {
        registerForm,
        registerFormRef,
        countdown,
        RegisterClick,
        sendVerificationCode,
        goBack,
        rules,
      };
    },
  
    beforeCreate() {
      document.body.style.margin = "0";
    },
  };
  </script>
  
  
  <style src="../assets/base.css"></style>
  <style scoped>
    .container {
      display: flex;
      align-items: center;
      height: 100vh;
      margin: 0px;
      padding: 0px;
      border: 0px;
    }
  
    element.style {
      width: 100%;
      margin-top: 0px;
    }
  
    h1 {
      font-size: 28px;
      text-align: center;
      color:#576251
    }
  
    :deep(.el-tabs__active-bar) {
      background-color: #549247 !important;
      }
  
    :deep(.el-tabs__item.is-active) {
      color: #549247 !important;
    }
  
    :deep(.el-tabs__item:hover) {
        color: #549247;
        cursor: pointer;
    }
  
    el-input{
      padding-top: 50px;
      padding-bottom: 5px;
  
    }
  
  </style>
  
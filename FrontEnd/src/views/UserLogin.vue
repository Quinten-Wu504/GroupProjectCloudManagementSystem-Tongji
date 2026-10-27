<template>
  <div class="main" style="background-color: #f2f8ee; height: 100vh; width: 100vw">
    <div class="container">
      <el-row style="width: 100vw; justify-content: center;">
        <el-col :span="14">
          <img src="../assets/image3.jpg" alt="" style="width: 80%;" />
        </el-col>

        <el-col :span="10">
          <el-card style="width:80%; border-radius:2vh; height: 100%;">
            <template v-if="!isAdminView">
              <!-- 用户登录 -->
              <h1>欢迎使用<br>小组项目云管理系统</h1>
              <el-tabs v-model="activeTab" class="demo-tabs" style="padding: 0% 8%;">
                <el-tab-pane label="密码登录" name="password">
                  <el-form :model="passwordForm" :rules="rules1" ref="passwordFormRef" @submit.prevent="handlePasswordLogin">
                    <el-form-item  prop="account">
                      <el-input placeholder="请输入账号/手机号码" v-model="passwordForm.account" clearable />
                    </el-form-item>
                    <el-form-item prop="password">
                      <el-input placeholder="请输入密码" v-model="passwordForm.password" type="password" show-password />
                    </el-form-item>
                    <el-form-item>
                      <button class="btn1" style="width: 100%; margin-top: 3%">登录</button>
                    </el-form-item>
                  </el-form>
                </el-tab-pane>

                <el-tab-pane label="验证码登录" name="verification">
                  <el-form :model="verificationForm" :rules="rules2" ref="verificationFormRef">
                    <el-form-item  prop="phoneNumber">
                      <el-input placeholder="请输入手机号码" v-model="verificationForm.phoneNumber" clearable />
                    </el-form-item>
                    <el-form-item prop="code">
                      <el-input placeholder="请输入验证码" v-model="verificationForm.code" style="width: 60%; margin-right: 5%;" />
                      <button type="button" class="btn2" :disabled="countdown > 0" style="width: 35%; margin-left: 0%; margin-top: 2%; height: 90%; font-size: 100%;" @click="sendVerificationCode">
                        {{ countdown > 0 ? `重新发送(${countdown})` : '发送验证码' }}
                      </button>
                    </el-form-item>
                    <el-form-item>
                      <button type="button" class="btn1" @click="handleVerificationLogin" style="width: 100%; margin-top: 3%">登录</button>
                    </el-form-item>
                  </el-form>
                </el-tab-pane>
              </el-tabs>

              <el-row style="width: 100%; margin-top: 15%;">
                <el-col :span="12">
                  <h5 style="margin: 0% 0% 5% 15%; text-align: left;">
                    还没有账号？
                    <span @click="goToSignUpView" style="cursor: pointer; text-decoration: underline; color: #549247;">点我注册</span>
                  </h5>
                </el-col>
                <!-- <el-col :span="12">
                  <h5 @click="goToAdminView" style="cursor: pointer; color: #549247; margin: 0% 15% 5% 0%; text-align: right">管理员登录</h5>
                </el-col> -->
              </el-row>
            </template>

            <template v-else>
              <!-- 管理员登录页面 -->
              <!-- <h5 @click="goBack" style="cursor: pointer; color: #549247; text-align: left; margin: 10px 0px 0px 0px;">
                返回
              </h5>
              <h1 style="margin-top: 10px;">管理员登录</h1>
              <el-form :model="adminForm" style="padding: 0% 8%;" :rules="rules3" ref="adminFormRef" @submit.prevent="handleAdminLogin">
                <el-form-item prop="adminUser">
                  <el-input placeholder="请输入管理员账号" v-model="adminForm.adminUser" clearable />
                </el-form-item>
                <el-form-item prop="adminPassword">
                  <el-input placeholder="请输入密码" v-model="adminForm.adminPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <button class="btn1" style="width: 100%; margin-top: 3%">登录</button>
                </el-form-item>
              </el-form> -->
            </template>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import router from '@/router';
import { verificationLogin, passwordLogin, sendVerificationCodeApi } from '@/api/api';  // 添加发送验证码API
import { useStore } from 'vuex';


export default {
  setup() {
    const store = useStore();
    const activeTab = ref('password');
    const isAdminView = ref(false);

    const passwordFormRef = ref(null);
    const verificationFormRef = ref(null);

    const passwordForm = reactive({
      account: '',
      password: '',
    });

    const verificationForm = reactive({
      phoneNumber: '',
      code: '',
    });

    const adminForm = reactive({
      adminUser: '',
      adminPassword: '',
    });

    const countdown = ref(0);
    const countdownTimer = ref(null);

    // 验证手机号格式
    const checkPhoneNumber = (rule, value, callback) => {  
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (phoneRegex.test(value)) {  
        callback(); 
      } else {
        callback(new Error('手机号格式不正确')); 
      }
    };

    //表单格式验证
    const rules1 = {
      account: [{ required: true, message: '账号/手机号码不能为空', trigger: 'blur' }],
      password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
    };

    const rules2 = {
      phoneNumber: [{ required: true, message: '电话号码不能为空', trigger: 'blur' },
                { validator: checkPhoneNumber, trigger: 'blur' }],
      code: [{ required: true, message: '验证码不能为空', trigger: 'blur' }]
    };

    const rules3 = {
      adminUser: [{ required: true, message: '管理员账号不能为空', trigger: 'blur' }],
      adminPassword: [{ required: true, message: '管理员密码不能为空', trigger: 'blur' }],
    };

    // 发送验证码
    const sendVerificationCode = async () => {
      if (!verificationForm.phoneNumber) {
        ElMessage.warning('请输入手机号');
        return;
      }
      else if (!/^1[3456789]\d{9}$/.test(verificationForm.phoneNumber)) {
        ElMessage.warning('请输入正确格式的手机号');
        return;
      }
      try {
        // 验证码接口待确认
        const formData = new FormData();
        formData.append('phoneNumber', verificationForm.phoneNumber);
        formData.append('choice', 1);
        await sendVerificationCodeApi(formData);
        ElMessage.success('验证码发送成功');
        startCountdown();
      } catch (error) {
        console.error('发送验证码失败:', error);
        ElMessage.error('验证码发送失败');
      }
    };

    const startCountdown = () => {
      countdown.value = 60;
      countdownTimer.value = setInterval(() => {
        if (countdown.value > 0) {
          countdown.value--;
        } else {
          clearInterval(countdownTimer.value);
        }
      }, 1000);
    };

    // 账号密码登录
    const handlePasswordLogin = async () => {
      try {
        await passwordFormRef.value.validate();
        const isPhoneNumber = /^1[3-9]\d{9}$/.test(passwordForm.account);
        const loginParams = isPhoneNumber
          ? { phoneNumber: passwordForm.account, password: passwordForm.password }
          : { account: passwordForm.account, password: passwordForm.password };

        const response = await passwordLogin(loginParams);
        if (response.data.success) {
          ElMessage.success('登录成功');
          router.push({ path:'/PersonalPage', });
          store.commit('setUser', response.data.data); // 更新用户信息
        } else {
          console.log(response.data.message);
          ElMessage.error(response.data.message || '登录失败');
        }
      } catch (error) {
        ElMessage.error('请填写完整信息');
      }
    };

    // 验证码登录
    const handleVerificationLogin = async () => {
      try {
        await verificationFormRef.value.validate();
        const response = await verificationLogin(verificationForm);
        if (response.data.success) {
          ElMessage.success('登录成功');
          router.push({ path:'/PersonalPage', });
          console.log(response);
          store.commit('setUser', response.data.data); // 更新用户信息
        } else {
          ElMessage.error(response.data.message || '登录失败');
        }
      } catch (error) {
        ElMessage.error('请填写完整信息');
      }
    };

    const handleAdminLogin = async () => {
      // 管理员登录处理
    };

    const goToSignUpView = () => {
      ElMessage.success('去注册');
      router.push('/UserRegister');
    };

    const goToAdminView = () => {
      isAdminView.value = true;
    };

    // const goBack = () => {
    //   isAdminView.value = false;
    // };

    return {
      activeTab,
      isAdminView,
      passwordForm,
      verificationForm,
      passwordFormRef,
      verificationFormRef,
      adminForm,
      countdown,
      handlePasswordLogin,
      handleVerificationLogin,
      handleAdminLogin,
      sendVerificationCode,
      goToAdminView,
      goToSignUpView,
      //goBack,
      rules1,
      rules2,
      rules3,
    };
  },

  beforeCreate() {
    document.body.style.margin = '0';
  },
};
</script>

<style src="../assets/base.css"></style>
<style scoped>
  .container {
    display: flex;
    align-items: center;
    height: 100vh;
    width: 100%;
    margin: 0px;
    padding: 0px;
    border: 0px;
  }

  h1 {
    font-size: 6vh;
    text-align: center;
    color: #576251;
  }

  :deep(.el-tabs__item) {
    font-size: 120%;
    margin-top: 3%;
    margin-bottom: 0%;
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

  :deep(.el-input) {
    font-size: 100%;
    height: 90%;
    margin-top: 2%;
    margin-bottom: 0vh;
  }

  :deep(.el-form-item__content) {
    display: flex;
    justify-content: center;
  }
</style>

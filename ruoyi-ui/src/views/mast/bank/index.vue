<template>
  <div class="app-container">
    <!-- <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form> -->

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['mast:bank:add']"
          style="margin-left: 10px;"
        >新建</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['mast:bank:edit']"
        >修改</el-button>
      </el-col> -->
      <!-- <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['mast:bank:remove']"
        >删除</el-button>
      </el-col> -->
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mast:bank:export']"
        >导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bankList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="银行序号" align="center" prop="bankId" /> -->
      <el-table-column label="银行编码" align="center" prop="bankCode" />
      <el-table-column label="银行名称" align="center" prop="bankName" />
      <el-table-column label="银行简称" align="center" prop="bankAbbreviation" />
      <el-table-column label="银行类型" align="center" prop="bankType" >
        <template slot-scope="scope">
          <span v-for="item in bank_typeList" :key="index">
            <template v-if="scope.row.bankType===item.bankTypeId">
              {{item.bankType}}
            </template>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="是否启用" align="center" prop="enable">
       <template slot-scope="scope">
        <el-checkbox
         :value="enableStatus[scope.$index]"
         :disabled="true"
        ></el-checkbox>
       </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mast:bank:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mast:bank:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改银行对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="银行编码" prop="bankCode">
          <el-input v-model="form.bankCode" placeholder="请输入银行编码" />
        </el-form-item>
        <el-form-item label="银行名称" prop="bankName">
          <el-input v-model="form.bankName" placeholder="请输入银行名称" />
        </el-form-item>
        <el-form-item label="银行简称" prop="bankAbbreviation">
          <el-input v-model="form.bankAbbreviation" placeholder="请输入银行简称" />
        </el-form-item>
        <el-form-item label="银行类型" prop="bankType">
          <el-select v-model="form.bankType" placeholder="请选择银行类型" >
            <el-option
              v-for="dict in bank_typeList"
              :key="dict.bankTypeId"
              :label="dict.bankType"
              :value="dict.bankTypeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="启用" prop="enable">
        <el-checkbox v-model="form.enable" ></el-checkbox>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBank, getBank, delBank, addBank, updateBank,listBank_type } from "@/api/mast/bank";

export default {
  // props:['values'],
  // computed:{
  //   isChecked:{
  //     get(){
  //       return this.value>0
  //     },
  //     set(val){
  //       this.$emit('input',val?1:0);
  //     }
  //   }
  // },
  name: "Bank",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 银行表格数据
      bankList: [],
      // form:{
      //   bankType:'',//初始化银行类型为空
      // },
      bank_typeList: [],
      enableStatus: [], // 用于存储复选框选中状态的数组
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  // mounted() {
  //   this.fetchBankTypes(); // 在组件挂载后执行数据库查询
  // },
  created() {
    this.getList();
    this.getList1();
  },
  methods: {
    /** 查询银行列表 */
    getList() {
      this.loading = true;
      listBank(this.queryParams).then(response => {
        this.bankList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.enableStatus = this.bankList.map((account) => account.enable === 1);
      });
    },
    /** 查询银行类型列表 */
    getList1() {
      listBank_type().then(response => {
        this.bank_typeList = response.rows;
        this.total = response.total;
      });
    },
    // fetchBankTypes() {
    //   // 执行数据库查询，获取银行类型数据
    //   axios.get('/mast/bank_type/list')
    //     .then(response => {
    //       this.bankTypes = response.data; // 将查询结果赋值给 bankTypes
    //     })
    //     .catch(error => {
    //       console.error(error);
    //     });
    // },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        bankId: null,
        bankCode: null,
        bankName: null,
        bankAbbreviation: null,
        bankType: null,
        enable: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.bankId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加银行";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const bankId = row.bankId || this.ids
      getBank(bankId).then(response => {
        if(response.data.enable==1){
          response.data.enable=true
        }else{
          response.data.enable=false
        }
        this.form = response.data;
        this.open = true;
        this.title = "修改银行";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.bankId != null) {
            if(this.form.enable==true){
              this.form.enable=1
            }else{
              this.form.enable=0
            }
            updateBank(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            if(this.form.enable==true){
              this.form.enable=1
            }else{
              this.form.enable=0
            }
            addBank(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const bankIds = row.bankId || this.ids;
      this.$modal.confirm('是否确认删除银行编号为"' + bankIds + '"的数据项？').then(function() {
        return delBank(bankIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    // handleExport() {
    //   this.download('mast/bank/export', {
    //     ...this.queryParams
    //   }, `bank_${new Date().getTime()}.xlsx`)
    // }
  }
};
</script>


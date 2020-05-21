$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'config/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: "id", hidden: true, key: true},
            {label: 'Task', name: 'taskName', width: 45},
            {label: '源IP', name: 'srcIp', width: 75},
            {label: '源端口', name: 'srcPort', width: 75},
            {label: '源用户名', name: 'srcName', width: 75},
            {label: '源密码', name: 'srcPassword', width: 75},
            {label: '目标IP', name: 'destIp', width: 75},
            {label: '目标端口', name: 'destPort', width: 75},
            {label: '目标用户名', name: 'destName', width: 75},
            {label: '目标密码', name: 'destPassword', width: 75},
        ],
        viewrecords: true,
        height: 385,
        // rowNum: 10,
        // rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        // pager: "#jqGridPager",
        jsonReader: {
            root: "data",
            // page: "page.currPage",
            // total: "page.totalPage",
            // records: "page.totalCount"
        },
        // prmNames: {
        //     page: "page",
        //     rows: "limit",
        //     order: "order"
        // },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
            $("#cb_jqGrid").click();
        }
});

    new AjaxUpload('#upload', {
        action: baseURL + 'upload',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit: function (file, extension) {
            // if (vm.config.type == null) {
            //     alert("云存储配置未配置");
            //     return false;
            // }

            vm.filePath = file;
            if (!(extension && /^(xlsx)$/.test(extension.toLowerCase()))) {
                alert('只支持xlsx格式！');
                return false;
            }
        },
        onComplete: function (file, r) {
            if (r.code == 0) {
                // alert(r.data);
                vm.reload();
            } else {
                alert(r.msg);
            }
        }
    });


    $('#myModal').on('hide.bs.modal',
        function() {
            window.location.href='result.html';
        })
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        roleList: {},
        filePath: null,
        param: {
            countPer: 30,
            contentPer: 30,
            limitCount: 10000,
            selectedIdList: []
        }
    },

    methods: {
        createTask: function() {
            if (vm.validator()) {
                return;
            }

            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }
            vm.param.selectedIdList = ids;

            $.ajax({
                type: "POST",
                url: baseURL + "start",
                contentType: "application/json",
                data: JSON.stringify(vm.param),
                success: function (r) {
                    if (r.code === 0) {
                        // parent.location.href ='result.html';
                        $('#myModal').modal('show')
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },

        reload: function () {
            vm.showList = true;
            $("#jqGrid").trigger("reloadGrid");
        },
        validator: function () {
            if (isBlank(vm.param.countPer)) {
                alert("数据量检测%不能为空");
                return true;
            }

            if (vm.param.countPer > 100 || vm.param.countPer <= 0) {
                alert("数据量检测%有效范围为(0,100]");
                return true;
            }

            if (isBlank(vm.param.contentPer)) {
                alert("数据量检测%不能为空");
                return true;
            }

            if (vm.param.contentPer > 100 || vm.param.contentPer <= 0) {
                alert("数据量检测%有效范围为(0,100]");
                return true;
            }

            if (isBlank(vm.param.limitCount)) {
                vm.param.limitCount = 10000;
            }
        }
    }
});
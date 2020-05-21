var timerHanlder = null;
var countData = null;


$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + '/count/result',
        datatype: "json",
        colModel: [
            {label: 'Task', name: 'taskName', width: 75},
            {label: '库名', name: 'dbName', width: 75},
            {label: '表情况', name: 'srcPort', width: 75, formatter: stateFormatter},
            {label: '数据量检查', name: 'result', width: 75, formatter: resultFormatter}
        ],
        viewrecords: true,
        height: 385,
        // rowNum: 10,
        // rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: false,
        // pager: "#jqGridPager",
        jsonReader: {
            root: "data.resultList",
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
        },
        loadComplete: function (data) {
            console.log(data);
            if (data.code == 0) {
                countData = data.data;
                if (countData != null && countData.state != null) {
                    if (countData.state == 0) {
                        vm.taskState = "执行中...";
                        startTimerIfNotStart();
                    } else {
                        vm.taskState = "执行完成";
                        window.clearInterval(timerHanlder);
                    }
                }
            }
        }
    });

    $("#jqGrid2").jqGrid({
        url: baseURL + '/content/result',
        datatype: "json",
        colModel: [
            {label: 'Task', name: 'taskName', width: 75},
            {label: '库名', name: 'dbName', width: 75},
            {label: '表情况', name: 'srcPort', width: 75, formatter: stateFormatter},
            {label: '内容检测结果', name: 'result', width: 75, formatter: resultFormatter}
        ],
        viewrecords: true,
        height: 385,
        // rowNum: 10,
        // rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: false,
        // pager: "#jqGridPager",
        jsonReader: {
            root: "data.resultList",
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
            $("#jqGrid2").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }});

});

//自定义报警列格式
function resultFormatter(cellvalue, options, rowdata) {
    if (cellvalue == 1)
        return '成功';
    else
        return '<span style="color: red">失败&nbsp;&nbsp;</span><a href="'+rowdata.downloadUrl+'">下载未通过列表</a>';
}


function stateFormatter(cellvalue, options, rowdata) {
    var html = "<strong>"+rowdata.totalTableNum + "/</strong>";
    html += "<strong style='color: #00a65a'>"+rowdata.succTableNum + "/</strong>";
    if (rowdata.failedTableNum > 0) {
        html += "<strong style='color: red'>"+rowdata.failedTableNum + "</strong>";
    }  else {
        html += "<strong>"+rowdata.failedTableNum + "</strong>";
    }

    return html;
}

function startTimerIfNotStart() {
    if (timerHanlder == null) {
        timerHanlder = self.setInterval("refreshResult()",1000);
    }
}

function refreshResult() {
    $("#jqGrid").trigger("reloadGrid");
    $("#jqGrid2").trigger("reloadGrid");
}


var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        roleList: {},
        filePath: null,
        taskState: "无"
    },

    methods: {
        created() {
            $("#jqGrid").trigger("reloadGrid");
            $("#jqGrid2").trigger("reloadGrid");
        }
    }
});
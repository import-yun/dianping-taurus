var hostName;
$(function () {
    var sidebar = document.getElementById("sidebar");
    sidebar.style.display="none";
    $('li[id="host"]').addClass("active");
    $('#menu-toggler').on(ace.click_event, function() {
        sidebar.style.display="block";
        $('#sidebar').toggleClass('display');
        $(this).toggleClass('display');

        return false;
    });

    hostName = GetQueryString("hostName");
    fetch_Log();

});

function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}
function fetch_Log() {
    var $logContainer = $("#strout")


    $.ajax({
        url: "../attempts.do",
        data: {
            action: 'runlog',
            hostname:hostName,
            querytype:'agentlogs',
            status:status
        },
        timeout: 10000,
        type: 'POST',
        error: function () {
            $logContainer.text("没有找到日志数据");
        },
        success: function (response) {

            result = response.replace("\n","<br>");

            $logContainer.append("<div class=\"terminal-like\">"+response+"</div>");
            $logContainer.scrollTop($logContainer.get(0).scrollHeight);
            $(".loading").hide();
        },
        beforeSend:function(){//正在加载，显示“正在加载......”
            $(".loading").show();
        }

    });

}


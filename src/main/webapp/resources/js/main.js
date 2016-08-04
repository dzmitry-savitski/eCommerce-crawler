function showProgress(start) {
    if (start) {
        setInterval(function () {
            updateProgressBar();
        }, 1000);
    }

}

function updateProgressBar() {
    $.get("/getProgress", function (data) {
        /* update the progress bar width */
        $("#progress").width(data + '%');
        /* and display the numeric value */
        $("#progress").html(data + '%');

        /* test to see if the job has completed */
        if (data > 99.99) {
            clearInterval(progresspump);
            $("#progressouter").removeClass("active");
            $("#progress").html("Done");
        }
    })
}

function scrollLog() {
    $('#logFrame').contents().scrollTop($('#logFrame').contents().height());
}
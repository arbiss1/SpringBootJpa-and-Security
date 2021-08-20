  function deleteOrder(id) {
    swal({
      title: "Are you sure?",
      text: "All data will be deleted",
      icon: "warning",
      buttons: true,
      dangerMode: true,
    }).then((Delete) => {
      if (Delete)
        window.location.href = "/delete/" + id;
    });
  }
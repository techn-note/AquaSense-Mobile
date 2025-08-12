export function showModal(modalRef, title, message) {
    modalRef.current.setModalVisible(true);
    modalRef.current.setTitle(title);
    modalRef.current.setMessage(message);
}

export function hideModal(modalRef) {
    modalRef.current.setModalVisible(false);
}




var menu = {
    getMenu: function(){
        return [
            { title: 'Каталог', url: 'product'},
            { title: 'О нас', url: 'about' },
            { title: 'Контакты', url: 'contact' },
            { title: 'Login', action: 'login()',displayCondition:{auth:false}},
            { title: 'LogOut', action: 'logout()',displayCondition:{auth:true}},
            { title:'Администрирование', displayCondition: {auth:true,admin:true} , menu:[
                { title:'Заказы', url:'orders' },
                { title:'Клиенты', url:'person' },
                { title:'Товар', url:'item' },
                { title:'Поставщики', url:'company' }
            ]}
        ]
    }
};


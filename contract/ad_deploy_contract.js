"use strict";

/*
AdContractItem 代表一个广告
balance：广告主发布广告金额（可以为0）
nasPerShare: 每次分享成功后获取的nas（可以为0）
data：广告主体
    title
    desc
    img
    from
    time
*/
var AdContractItem = function (text) {
    if (text) {
        var obj = JSON.parse(text);
        this.data = obj.data;
        this.balance = obj.balance;
        this.nasPerShare = obj.nasPerShare;
    } else {
        this.balance = "";
        this.nasPerShare = "";
        this.data = "";
    }
};

AdContractItem.prototype = {
    toString: function () {
        return JSON.stringify(this);
    }
};

var AdContractList = function () {
    LocalContractStorage.defineMapProperty(this, "adContracts", {
        parse: function (text) {
            return new AdContractItem(text);
        },
        stringify: function (o) {
            return o.toString();
        }
    });
    LocalContractStorage.defineMapProperty(this, "arrayMap");
    LocalContractStorage.defineProperty(this, "size");

};

AdContractList.prototype = {
    init: function () {
        this.size = 0;
    },
    len:function(){
        return this.size;
    },

    save: function (key, value) {
        if (key === "") {
            throw new Error("empty key or value");
        }
        var adContractItem = this.adContracts.get(key);
        if (adContractItem) {
            throw new Error("ad contract has already been exist");
        }

        adContractItem = new AdContractItem(value);
        if (!adContractItem) {
            throw new Error("invalid value");
        }
        var index = this.size;
        this.arrayMap.set(index, key);
        this.adContracts.set(key, adContractItem);
        this.size +=1;
        return this.getAdContract(key)
    },

    deletAd: function (key) {
        key = key.trim();
        if (key === "") {
            throw new Error("empty key");
        }
        var adContractItem = this.adContracts.get(key);

        if (!adContractItem) {
            throw new Error("ad contract not exist");
        }
        return this.adContracts.del(key);
    },

    getAdList:function(){
        return JSON.stringify(this.adContracts);
    },

    getAdContract: function (key) {
        key = key.trim();
        if (key === "") {
            throw new Error("empty key");
        }
        var adContractItem = this.adContracts.get(key);

        if (!adContractItem) {
            throw new Error("ad contract not exist");
        }
        return JSON.stringify(adContractItem);
    },

    forEach: function(limit, offset){
        if(offset> this.size){
            throw new Error("offset is not valid");
        }
        var number = offset+limit;
        if(number > this.size){
            number = this.size;
        }
        var result  = "";
        for(var i=offset;i<number;i++){
            var key = this.arrayMap.get(i);
            var object = this.adContracts.get(key);
            result +=  object + "----------------------";
        }
        return result;
    },

    transfer: function (address, value) {
        var result = Blockchain.transfer(address, value);
        console.log("transfer result:", result);
        Event.Trigger("transfer", {
            Transfer: {
                from: Blockchain.transaction.to,
                to: address,
                value: value
            }
        });

        return result;
    }
};
module.exports = AdContractList;



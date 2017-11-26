
var EasyTotalizer = artifacts.require("EasyTotalizer");

const TEST_TITLE = "test title";
const TEST_DECRIPTION = "test descr";
const TEST_MIN_BET = "2000000000000000000";
const TEST_PERCENT = 5;
const TEST_VARIANTS = ["Variant 1", "Variant 2"];

contract("EasyTotalizer", function(accounts) {
    beforeEach("init", function() {
        EasyTotalizer.deployed().then(function(instance){
            instance.init(
                TEST_TITLE,
                TEST_DECRIPTION,
                TEST_MIN_BET,
                TEST_PERCENT,
                TEST_VARIANTS,
                {from: accounts[0]}
            );
        }).catch(function(error){
            console.log(error);
        });
    });
    it("Title", function() {
        EasyTotalizer.deployed().then(function(instance){
            return instance.title.call();
        }).then(function(title) {
            assert.equal(title.toString(), TEST_TITLE, "Title isn't equal");
        });
    });
    it("Organizer", function() {
        EasyTotalizer.deployed().then(function(instance){
            return instance.organizer.call();
        }).then(function(organizer){
            assert.equal(organizer, accounts[0]);
        });
    });
    it("Min Bet", function() {
        EasyTotalizer.deployed().then(function(instance){
            return instance.minimumBet.call();
        }).then(function(minimumBet){
            assert.equal(minimumBet.toNumber(), TEST_MIN_BET, "Minimum bet isn't equal");
        });
    });
    it("Bet", function(){
        EasyTotalizer.deployed().then(function(instance){
            
        });
    });
    it("Kill", function() {
        EasyTotalizer.deployed().then(function(instance){
            instance.kill.call();
        });
    });
    // it("Init", function() {
    //     return EasyTotalizer.deployed().then(function(instance){
    //         instance.init.call(
    //             TEST_TITLE,
    //             TEST_DECRIPTION,
    //             TEST_MIN_BET,
    //             TEST_PERCENT,
    //             TEST_VARIANTS
    //         );
    //     });
    // });

});
const resendCode = document.getElementById("resendCode");
var viewTime = document.querySelector("#viewTime");

resendCode.style.pointerEvents = "none";
resendCode.classList.add("disabled");

window.addEventListener("load", function () {
  new Timer(1, viewTime, function () {
    //enable
    //alter url
    resendCode.classList.remove("disabled");
    viewTime.classList.add("hidden");
    resendCode.style.pointerEvents = "all";
  }).start();
});

function Timer(mins, target, cb) {
  this.counter = mins * 120;
  this.target = target;
  this.callback = cb;
}
Timer.prototype.pad = function (s) {
  return s < 10 ? "0" + s : s;
};
Timer.prototype.start = function (s) {
  this.count();
};
Timer.prototype.stop = function (s) {
  this.count();
};
Timer.prototype.done = function (s) {
  if (this.callback) this.callback();
};
Timer.prototype.display = function (s) {
  this.target.innerHTML = this.pad(s);
};
Timer.prototype.count = function (s) {
  var self = this;
  self.display.call(self, self.counter);
  self.counter--;
  var clock = setInterval(function () {
    self.display(self.counter);
    self.counter--;
    if (self.counter < 0) {
      clearInterval(clock);
      self.done.call(self);
    }
  }, 1000);
};

var UnexpectedStatusError = function(expectedStatus, actualStatus) {
  if (arguments.length > 1) {
    this.expectedStatus = expectedStatus;
    this.status = actualStatus;
    this.message = "Expected: " + expectedStatus + ", actual: " + actualStatus;
  } else {
    this.status = expectedStatus;
    this.message = "Unexpected status: "+ this.status;
  }
  this.name = "UnexpectedStatusError";
};
UnexpectedStatusError.prototype = Error.prototype;

module.exports = UnexpectedStatusError;

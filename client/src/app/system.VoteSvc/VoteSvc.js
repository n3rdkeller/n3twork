(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('VoteSvc', VoteSvc);


  VoteSvc.$inject = ['APISvc', '$q', '$modal'];
  function VoteSvc(APISvc, $q, $modal) {
    var service = {
      showVotes: showVotes,
      voteAction: voteAction
    };
    return service;

    function showVotes(postID) {
      var modalInstance = $modal.open({
        templateUrl: 'app/views/votesModal.html',
        controller: 'ShowVotesCtrl',
        controllerAs: 'votes',
        size: 'sm',
        resolve: {
          getPostID: function() {
            return postID;
          }
        }
      });
    }

    function voteAction(postID, addOrRemove) {
      var deferred = $q.defer();

      APISvc.request({
        method: 'POST',
        url: '/post/vote/' + (addOrRemove ? 'remove' : 'add'),
        data: { 'id': postID }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.successful);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

  }
})();
